package project.meapy.meapy;

import android.app.Application;
import android.content.Context;

import project.meapy.meapy.groups.joined.MyGroupsActivity;

/**
 * Created by tarek on 20/03/18.
 */

public class MyApplication extends Application{
    private static Context c;
    public MyApplication(){
        super();
        c = this;
    }

    public static void launch(){
        NotificationThread t = new NotificationThread(c);
        t.start();
    }
}
