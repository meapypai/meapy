package project.meapy.meapy.groups;

/**
 * Created by sansaoui on 19/02/18.
 */

public class DiscussionGroup {

    private int idDrawable;
    private String name;
    private String lastMessage;
    private String nameRef; //  key sub directory + file name

    public DiscussionGroup(int id, String name, String lastMessage, String nameRef) {
        this.idDrawable = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.nameRef = nameRef;
    }

    public String getNameRef() {
        return nameRef;
    }

    public void setNameRef(String nameRef) {
        this.nameRef = nameRef;
    }

    public int getDrawable() {
        return idDrawable;
    }

    public void setDrawable(int img) {
        this.idDrawable = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}


