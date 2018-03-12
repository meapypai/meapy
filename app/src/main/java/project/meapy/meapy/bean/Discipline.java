package project.meapy.meapy.bean;

/**
 * Created by senoussi on 12/03/18.
 */

public class Discipline extends DomainObject {
    String name;
    public Discipline(){super();}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){return name;}

}
