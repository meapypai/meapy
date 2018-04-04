package project.meapy.meapy.bean;

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
}
