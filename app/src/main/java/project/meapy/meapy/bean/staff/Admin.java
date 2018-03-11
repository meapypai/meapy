package project.meapy.meapy.bean.staff;

import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 11/03/2018.
 */

public abstract class Admin extends User {

    public Admin(String firstName, String lastName, String password, String email) {
        super(firstName, lastName, password, email);
    }
}
