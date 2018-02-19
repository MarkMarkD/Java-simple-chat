import java.io.Serializable;
import java.util.Date;

/**
 * Created by Dmitriy on 08.02.2018.
 */
public class Message implements Serializable{
    private String messageText;
    private Date date;
    private String name;
    private String time;

    public Message(String name, String messageText) {
        this.name = name;
        this.messageText = messageText;
        date = new Date();
        String[] splitDate = date.toString().split(" ");
        time = splitDate[3];
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return (name + " (" + time + "): " + messageText);
    }
}

