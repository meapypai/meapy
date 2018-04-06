package project.meapy.meapy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import project.meapy.meapy.bean.User;
import project.meapy.meapy.storage.StoreBean;

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
        StoreBean.storeUser(u,c);
        user = u;
    }

    public static User getUser() {
        if(user == null){
            user = StoreBean.provideUser(c);
        }
        return user;
    }

    public static void launch(){
        NotificationThread t = new NotificationThread(c);
        t.start();
    }
}
