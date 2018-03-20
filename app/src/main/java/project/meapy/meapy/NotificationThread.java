package project.meapy.meapy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.groups.joined.MyGroupsActivity;

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
    }
}
