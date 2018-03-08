package project.meapy.meapy.bean;

/**
 * Created by tarek on 08/03/18.
 */

public class Post extends DomainObject{
    private int user_id;
    private String textContent;
    private int idFileAssocied;

    public Post() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getIdFileAssocied() {
        return idFileAssocied;
    }

    public void setIdFileAssocied(int idFileAssocied) {
        this.idFileAssocied = idFileAssocied;
    }
}
