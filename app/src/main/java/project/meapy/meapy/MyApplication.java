package project.meapy.meapy;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.storage.StoreBean;
import project.meapy.meapy.utils.firebase.UserLink;

/**
 * Created by tarek on 20/03/18.
 */

public class MyApplication extends Application{
    private static Context c;
    private static User user;
    private static NotificationThread notifThread;
    private static Stack<MyAppCompatActivity> stackActivity = new Stack<>();

    public MyApplication(){
        super();
        c = this;
        //startThreadLock(); // THREAD TO LOCK
    }

    public static void addActivity(MyAppCompatActivity act){
        stackActivity.push(act);
    }

    public static MyAppCompatActivity removeActivity(){
        if(getCurrentActivity() != null)
            return stackActivity.pop();
        return null;
    }

    public static MyAppCompatActivity getCurrentActivity(){
        MyAppCompatActivity act = null;
        if(!stackActivity.empty())
            act = stackActivity.peek();
        return act;
    }

    public static void setUser(User u) {
        if(u != null && u.getTimeRegistration() == null){ // A REVOIR
            u.setTimeRegistration(new Date());
            UserLink.putTimeRegistration(u);
        }
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
        if(notifThread == null) {
            notifThread = new NotificationThread(c);
            notifThread.start();
        }
    }

    public static void restartNotifThread(){
        if(notifThread != null){
            notifThread.interrupt();
            launch();
        }
    }

    public static void sendDialogAlertLock(){
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            public void run() {
                if(true) {
                    MyAppCompatActivity act = getCurrentActivity();
                    if(act != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
                        builder.setMessage("The trial period of this version has expired ");
                        DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for(MyAppCompatActivity act : stackActivity){
                                    act.finish();
                                }
                                throw  new RuntimeException("expired app");
                            }
                        };
                        builder.setPositiveButton("Ok", onClick);
                        AlertDialog dialog = builder.create();

                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                for(MyAppCompatActivity act : stackActivity) {
                                    act.finish();
                                }
                                throw  new RuntimeException("expired app");
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });
    }

    static int year=2018,month = 4, day = 24 ,hour = 17 ,min = 30;
    public static boolean checkDateFinish(){
        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        Calendar limit = Calendar.getInstance();
        limit.set(year,month,day,hour,min);
        Date curD = current.getTime();
        Date limD = limit.getTime();
        if(limD.before(curD) || limD.equals(curD)){
            return true;
        }
        return false;
    }

    public static void startThreadLock(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (checkDateFinish()) {
                        sendDialogAlertLock();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
