package chatapp.com.chatapp;

/**
 * Created by bhuvanes on 3/9/17.
 */

public class Message {
    private String type, message;
    private long time;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Message(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Message(long time, Boolean seen) {
        this.time = time;
        this.seen = seen;
    }

    private Boolean seen;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(String type, String seen, String from, String time, String message) {
        this.type = type;
        this.message = message;
    }
    public Message(){

    }
}
