package project.meapy.meapy.bean;

/**
 * Created by tarek on 08/03/18.
 */

public class Groups extends DomainObject{
    private String name;
    private int idUserAdmin;
    private int limitUsers;

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

    @Override
    public String toString() {
        return "Groups{" +
                "name='" + name + '\'' +
                ", idUserAdmin=" + idUserAdmin +
                ", limitUsers=" + limitUsers +
                '}';
    }
}
