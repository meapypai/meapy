package project.meapy.meapy.bean;

/**
 * Created by tarek on 08/03/18.
 */

public class Post extends DomainObject{
    private int user_id;
    private String textContent;
    private String filePath;
    private int groupId;
    private String title;
    private String disciplineName;
    private int disciplineId;

    public String getDisciplineName() {
        return disciplineName;
    }

    public void setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
    }

    public int getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(int disciplineId) {
        this.disciplineId = disciplineId;
    }

    public Post(){super();}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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
