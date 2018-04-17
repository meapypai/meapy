package project.meapy.meapy.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import project.meapy.meapy.NotificationThread;
import project.meapy.meapy.bean.Notifier;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class NotificationWorker{
    Context context;
    private NotificationManager manager;
    public NotificationWorker(Context c){
        context = c;
        manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
    }

    public void cancelNotifById(int id){
        manager.cancel(id);
    }

    public Notification make(Notifier notifier, Intent intent, int RidLogo, int idNotif, int flags){
        PendingIntent pendingIntent = getPendingIntentNotifier(notifier, intent);
        String title = notifier.getTitle();
        String content = notifier.getContent();
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "channel_name";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel("default", name, importance);
            mChannel.setDescription(description);

            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder buildr = new NotificationCompat.Builder(context, "default")
                    .setTicker(title)
                    .setSmallIcon(RidLogo)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent);

            notification = buildr.build();
            notification.flags = flags;
            manager.notify(idNotif,notification);
        }else{
            Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis())
                    .setTicker(title)
                    .setSmallIcon(RidLogo)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent);
            notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(idNotif, notification);
        }
        return notification;
    }

    private PendingIntent getPendingIntentNotifier(Notifier notif, Intent intent){
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context,new Random().nextInt(),intent,PendingIntent.FLAG_ONE_SHOT);
    }
}
