package project.meapy.meapy.bean;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by tarek on 08/03/18.
 */

public class DomainObject implements Serializable {
    private int id = new Random().nextInt();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
