import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dmitriy on 09.02.2018.
 */
public class ChatAppServer {

    private List<Connection> connections = new ArrayList<>();
    private List<Message> history = new ArrayList<>();

    public ChatAppServer() {
        try {
            ServerSocket ss = new ServerSocket(8888);
            System.out.println("Server started at port 8888");
            System.out.println("Waiting for connections...");
            while (true) {
                Socket sock = ss.accept();

                //Creates connection to a new Client, puts it into list of connections, starts it in new Thread
                Connection connection = new Connection(sock);
                addConnection(connection);
                System.out.println("Number of connected clients: " + connections.size());
                connection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addConnection (Connection con) {
        connections.add(con);
    }

    private synchronized void removeConnection (Connection con) {
        connections.remove(con);
    }

    private synchronized void addToHistory (Message message) {
        if (history.size() < 10) {
            history.add(message);
        }
        else {
            history.remove(9);
            history.add(message);
        }
    }

    //send history to a new Client
    private synchronized void sendHistory(Connection con) {
        try {
            Iterator<Message> iterator = history.iterator();
            while (iterator.hasNext()) {
                Message mes = iterator.next();
                con.oos.writeObject(mes);
                con.oos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Connection extends Thread {
        Socket sock;
        BufferedInputStream in;
        ObjectInputStream ois;
        BufferedOutputStream out;
        ObjectOutputStream oos;

        private void closeQuietly() {
            try {
                oos.close();
                ois.close();
                sock.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Connection (Socket sock) {
            this.sock = sock;
            System.out.println("Created new Connection thread");
            try {
                in = new BufferedInputStream(sock.getInputStream());
                ois = new ObjectInputStream(in);
                out = new BufferedOutputStream(sock.getOutputStream());
                oos = new ObjectOutputStream(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean historySent = false;
            Message messageFromClient = null;
            //ready to receive serialized messages from client
            while (true) {
                try {
                    messageFromClient = (Message) ois.readObject();
                    if (messageFromClient.getMessageText().equalsIgnoreCase("exit")) {
                        break;
                    }
                    else {
                        //sends history (last 10 messages) to a new connected client
                        if (historySent == false) {
                            sendHistory(this);
                            historySent = true;
                        }

                        addToHistory(messageFromClient);

                        //sends received message to all clients
                        synchronized (connections) {
                            Iterator<Connection> iterator = connections.iterator();
                            while (iterator.hasNext()) {
                                Connection con = iterator.next();
                                con.oos.writeObject(messageFromClient);
                                con.oos.flush();
                            }
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            Message exitMessage = new Message (messageFromClient.getName(), "has left");
            synchronized (connections) {
                removeConnection(this);
                System.out.println("Number of connected clients: " + connections.size());
                Iterator<Connection> exitIterator = connections.iterator();
                while (exitIterator.hasNext()) {
                    Connection con = exitIterator.next();
                    try {
                        con.oos.writeObject(exitMessage);
                        con.oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            closeQuietly();
        }
    }
}
