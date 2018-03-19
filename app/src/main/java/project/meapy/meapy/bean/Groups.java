package project.meapy.meapy.bean;

/**
 * Created by tarek on 08/03/18.
 */

public class Groups extends DomainObject{
    private String name;
    private int idUserAdmin;
    private int limitUsers;
    private String imageName;
    private String codeToJoin;

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

    public int getLimitUsers() {
        return limitUsers;
    }

    public void setLimitUsers(int limitUsers) {
        this.limitUsers = limitUsers;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return name;
    }
}
