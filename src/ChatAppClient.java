import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Dmitriy on 08.02.2018.
 */

public class ChatAppClient {
    private Scanner scanner;
    private Socket sock;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private BufferedInputStream reader;

    public ChatAppClient() {
        System.out.println("Client started");
        scanner = new Scanner(System.in);
        String name = readName();
        try {
            sock = new Socket("localhost", 8888);
            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            oos = new ObjectOutputStream(out);

            Message Greetings = new Message (name, "joins in");
            oos.writeObject(Greetings);
            oos.flush();

            //starts ServerListener as a daemon thread
            ServerListener listener = new ServerListener();
            listener.setDaemon(true);
            listener.start();

            System.out.println("Connection to Server established");
        } catch (IOException e) {
            System.out.println("Failed to connect Server");
            e.printStackTrace();
        }

        //reads messages and sends to server
        while (true) {
            String readAndSend = scanner.nextLine();
            Message message = new Message(name, readAndSend);
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (readAndSend.equalsIgnoreCase("exit")) {
                break;
            }
        }
        closeQuietly();
    }

    private void closeQuietly() {
        try {
            oos.close();
            ois.close();
            scanner.close();
            sock.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readName() {
        System.out.println("Please enter your name: ");
        return scanner.nextLine();
    }

    //daemon thread that listens to server
    public class ServerListener extends Thread {
        @Override
        public void run() {
            try {
                reader = new BufferedInputStream(sock.getInputStream());
                ois = new ObjectInputStream(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Message messageFromServer = (Message) ois.readObject();
                    System.out.println(messageFromServer);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}