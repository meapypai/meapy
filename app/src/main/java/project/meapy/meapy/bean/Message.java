package project.meapy.meapy.bean;

/**
 * Created by yassi on 13/03/2018.
 */

public class Message extends DomainObject {

    private String content;
    private String user;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message()  {
        super();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
