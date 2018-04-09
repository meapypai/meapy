package project.meapy.meapy.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import project.meapy.meapy.NotificationThread;

import static android.content.Context.NOTIFICATION_SERVICE;

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

    public Notification make(String title, String content, PendingIntent pendingIntent, int RidLogo, int idNotif, int flags){
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
}
