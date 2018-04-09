package project.meapy.meapy.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tarek on 08/03/18.
 */

public class Post extends DomainObject{
    private String user_uid;
    private String user;
    private String nameImageUser;
    private String textContent;
    private int nbPositiveMark;
    private int nbNegativeMark;
    //private String filePath;
    private List<String> filesPaths = new ArrayList<>();
    private int groupId;
    private String title;
    private String disciplineName;
    private int disciplineId;
    private Date date;

    public List<String> getFilesPaths() {
        return filesPaths;
    }

    public void setFilesPaths(List<String> filesPaths) {
        this.filesPaths = filesPaths;
    }

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
                "user_id=" + user_uid +
                ", textContent='" + textContent + '\'' +
                ", groupId=" + groupId +
                '}';
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNameImageUser() {
        return nameImageUser;
    }

    public void setNameImageUser(String nameImageUser) {
        this.nameImageUser = nameImageUser;
    }

    public int getNbPositiveMark() {
        return nbPositiveMark;
    }

    public void setNbPositiveMark(int nbPositiveMark) {
        this.nbPositiveMark = nbPositiveMark;
    }

    public int getNbNegativeMark() {
        return nbNegativeMark;
    }

    public void setNbNegativeMark(int nbNegativeMark) {
        this.nbNegativeMark = nbNegativeMark;
    }
}
