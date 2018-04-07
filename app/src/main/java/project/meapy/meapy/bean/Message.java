package project.meapy.meapy.bean;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yassi on 13/03/2018.
 */

public class Message extends DomainObject {

    private String content;
    private String nameUser;
    private String uIdUser;
    private Date date;
    private String colorNameUser;
    private Map<String,String> readByUsers = new HashMap<String,String>();

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

    public Map<String,String> getReadByUsers() {
        return readByUsers;
    }

    public boolean isReadedByUser(String uid){
        if(readByUsers != null) {
            return readByUsers.containsKey(uid);
        }return false;
    }

    public void setReadedBy(String uid){
        if(readByUsers != null)
            readByUsers.put(uid,uid);
    }
}
