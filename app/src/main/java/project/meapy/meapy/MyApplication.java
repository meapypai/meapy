package project.meapy.meapy;

import android.app.Application;
import android.content.Context;

import project.meapy.meapy.bean.User;

/**
 * Created by tarek on 20/03/18.
 */

public class MyApplication extends Application{
    private static Context c;
    private static User user;

    public MyApplication(){
        super();
        c = this;
    }

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }

    public static void launch(){
        NotificationThread t = new NotificationThread(c);
        t.start();
    }
}
