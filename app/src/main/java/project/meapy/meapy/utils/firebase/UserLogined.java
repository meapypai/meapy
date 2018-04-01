package project.meapy.meapy.utils.firebase;

import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 01/04/2018.
 */

public class UserLogined {

    private static UserLogined userLogined;
    private User user;

    private UserLogined() {}

    public static UserLogined getInstance() {
        if(userLogined == null) {
            userLogined = new UserLogined();
        }
        return userLogined;
    }

    public void setUserLogined(User u) {
        this.user = u;
    }

    public User getUserLogined() {
        return this.user;
    }
}