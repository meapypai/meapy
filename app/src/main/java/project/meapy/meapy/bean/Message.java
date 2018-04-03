package project.meapy.meapy.bean;

import java.util.Date;

/**
 * Created by yassi on 13/03/2018.
 */

public class Message extends DomainObject {

    private String content;
    private String nameUser;
    private String uIdUser;
    private Date date;
    private String colorNameUser;

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

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String user) {
        this.nameUser = user;
    }

    public String getuIdUser() {
        return uIdUser;
    }

    public void setuIdUser(String uIdUser) {
        this.uIdUser = uIdUser;
    }

    public String getColorNameUser() {
        return colorNameUser;
    }

    public void setColorNameUser(String colorNameUser) {
        this.colorNameUser = colorNameUser;
    }
}
