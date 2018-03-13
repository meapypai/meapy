package project.meapy.meapy.bean;

import java.util.Date;

/**
 * Created by yassi on 13/03/2018.
 */

public class Message extends DomainObject {

    private String content;
    private String user;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
