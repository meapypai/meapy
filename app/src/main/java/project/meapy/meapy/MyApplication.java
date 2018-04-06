package project.meapy.meapy;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.List;
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

    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(c.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(c.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void launch(){
        NotificationThread t = new NotificationThread(c);
        t.start();
    }
}
