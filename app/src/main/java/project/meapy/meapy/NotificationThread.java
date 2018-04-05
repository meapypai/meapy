package project.meapy.meapy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.MessageLink;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by yassi on 20/03/2018.
 */

public class NotificationThread extends Thread {

    Context context;
    public static final int REQUEST_NOTIFICATION = 5;
    public static final String ID_NOTIFICATION = "id_notification";

    public NotificationThread(Context context) {
        this.context =  context;
    }


    @Override
    public void run() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/notifications");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notifier notif = (Notifier) dataSnapshot.getValue(Notifier.class);
                //creation de la notification
                NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(context, MyGroupsActivity.class);
                intent.putExtra(ID_NOTIFICATION,notif.getId());

                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,REQUEST_NOTIFICATION,intent,PendingIntent.FLAG_ONE_SHOT);

                Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis())
                        .setTicker(notif.getTitle())
                        .setSmallIcon(R.drawable.logo_app1)
                        .setContentTitle(notif.getTitle())
                        .setContentText(notif.getContent())
                        .setContentIntent(pendingIntent);

                manager.notify(11,builder.build());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GroupLink.provideGroupsByCurrentuser(new RunnableWithParam() {
            @Override
            public void run() {
                final Groups grp = (Groups) getParam();
                MessageLink.getMessageByIdGroup(grp.getId()+"", new RunnableWithParam() {
                    @Override
                    public void run() {
                        Message msg = (Message) getParam();
                        //Notifier notif = (Notifier) dataSnapshot.getValue(Notifier.class);
                        //creation de la notification
                        NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
                        Intent intent = new Intent(context, ChatRoomActivity.class);
                        intent.putExtra(ID_NOTIFICATION,msg.getId());
                        intent.putExtra(OneGroupActivity.EXTRA_GROUP_ID,grp.getId()+"");
                        intent.putExtra(OneGroupActivity.EXTRA_GROUP_NAME,grp.getName());

                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context,REQUEST_NOTIFICATION,intent,PendingIntent.FLAG_ONE_SHOT);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // Create the NotificationChannel
                            CharSequence name = "channel_name";
                            String description = "desc";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel mChannel = new NotificationChannel("default", name, importance);
                            mChannel.setDescription(description);
                            // Register the channel with the system; you can't change the importance
                            // or other notification behaviors after this
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                                    NOTIFICATION_SERVICE);
                            notificationManager.createNotificationChannel(mChannel);
                            NotificationCompat.Builder buildr = new NotificationCompat.Builder(context, "default")
                                    .setTicker("title1")
                                    .setSmallIcon(R.drawable.logo_app1)
                                    .setContentTitle(msg.getNameUser())
                                    .setContentText(msg.getContent())
                                    .setContentIntent(pendingIntent);

                            notificationManager.notify(10,buildr.build());
                        }else{
                            Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis())
                                    .setTicker("title1")
                                    .setSmallIcon(R.drawable.logo_app1)
                                    .setContentTitle(msg.getNameUser())
                                    .setContentText(msg.getContent())
                                    .setContentIntent(pendingIntent);
                            manager.notify(11,builder.build());
                        }
                    }
                },null);
            }
        }, null);
    }
}
