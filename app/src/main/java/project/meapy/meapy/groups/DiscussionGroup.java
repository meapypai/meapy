package project.meapy.meapy.groups;

/**
 * Created by sansaoui on 19/02/18.
 */

public class DiscussionGroup {

    private int idDrawable;
    private String name;
    private String summary;
    private String nameRef; //  key sub directory + file name

    public DiscussionGroup(int id, String name, String summary, String nameRef) {
        this.idDrawable = id;
        this.name = name;
        this.summary = summary;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}


