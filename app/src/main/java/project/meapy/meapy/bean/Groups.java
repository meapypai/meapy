package project.meapy.meapy.bean;

import java.util.Objects;

/**
 * Created by tarek on 08/03/18.
 */

public class Groups extends DomainObject{
    private String name;
    private int idUserAdmin;
    private String imageName;
    private String codeToJoin;
    private String summary;

    public String getCodeToJoin() {
        return codeToJoin;
    }

    public void setCodeToJoin(String codeToJoin) {
        this.codeToJoin = codeToJoin;
    }

    public Groups(){super();}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdUserAdmin() {
        return idUserAdmin;
    }

    public void setIdUserAdmin(int idUserAdmin) {
        this.idUserAdmin = idUserAdmin;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Groups groups = (Groups) o;
        return idUserAdmin == groups.idUserAdmin &&
                Objects.equals(name, groups.name) &&
                Objects.equals(imageName, groups.imageName) &&
                Objects.equals(codeToJoin, groups.codeToJoin) &&
                Objects.equals(summary, groups.summary);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, idUserAdmin, imageName, codeToJoin, summary);
    }
}
