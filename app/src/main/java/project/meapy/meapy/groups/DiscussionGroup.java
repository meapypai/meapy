package project.meapy.meapy.groups;

import project.meapy.meapy.bean.DomainObject;

/**
 * Created by sansaoui on 19/02/18.
 */

public class DiscussionGroup extends DomainObject{

    private int idDrawable;
    private String name;
    private String summary;
    private String imageName;

    public DiscussionGroup(int id, String name, String summary, String imageName) {
        this.idDrawable = id;
        this.name = name;
        this.summary = summary;
        this.imageName = imageName;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}


