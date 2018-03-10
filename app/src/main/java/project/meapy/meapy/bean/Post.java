package project.meapy.meapy.bean;

/**
 * Created by tarek on 08/03/18.
 */

public class Post extends DomainObject{
    private int user_id;
    private String textContent;
    private String filePath;
    private int groupId;

    public int getGroupId() {
        return groupId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "user_id=" + user_id +
                ", textContent='" + textContent + '\'' +
                ", filePath='" + filePath + '\'' +
                ", groupId=" + groupId +
                '}';
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
