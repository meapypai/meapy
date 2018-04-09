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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.NotificationWorker;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.MessageLink;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.media.CamcorderProfile.get;

/**
 * Created by yassi on 20/03/2018.
 */

public class NotificationThread extends Thread {

    Context context;
    public static final int REQUEST_NOTIFICATION = 5;
    public static final String ID_NOTIFICATION = "id_notification";
    private static Map<Integer,Boolean> chatRoomStarted = new HashMap<>();

    private static final int LOGO_NOTIF = R.drawable.logo_app1_without_background;

    private static final int ID_NOTIFICATION_MESSAGE = 11;
    private static final int ID_NOTIFICATION_NOTIFIER = 1000;

    private static NotificationWorker worker ;

    private Map<Integer,Integer> idNotifMessageNotification = new HashMap<>();


    private List<Groups> idGroupToNotify = new GroupList();
    public NotificationThread(Context context) {
        this.context =  context;
        worker = new NotificationWorker(context);
    }

    public static void setStartedChatRoom(int idGroup, boolean b){
        chatRoomStarted.put(idGroup,b);
    }
    public static boolean isStartedChatRoom(int idGroup){
        Boolean b = chatRoomStarted.get(idGroup);
        if(b != null)
            return b;
        return false;
    }
    @Override
    public void run() {
        onNewNotif();
        onNewMessageReceived();

    }
    private void onNewMessageReceived(){
        GroupLink.provideGroupsByCurrentuser(new RunnableWithParam() {
            @Override
            public void run() {
                final Groups grp = (Groups) getParam();
                addGroupToNotify(grp);
                MessageLink.getLatestMessageByGroupId(grp.getId() + "", new RunnableWithParam() {
                    @Override
                    public void run() {
                        if (!isStartedChatRoom(grp.getId()) && isGroupToNotify(grp)) {
                            Message msg = (Message) getParam();
                            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                            if(fUser != null && !msg.isReadedByUser(fUser.getUid())) {
                                NotificationThread.this.notifyMessage(msg, grp);
                            }
                            //MediaPlayer mp = MediaPlayer.create(context,R.raw.intuition);
                            //mp.start();
                        }
                    }
                });
            }
        }, new RunnableWithParam() {
            @Override
            public void run() {
                final Groups grp = (Groups) getParam();
                removeGroupToNotify(grp);
            }
        });
    }

    private void addGroupToNotify(Groups grp){
        idGroupToNotify.add(grp);
    }

    private boolean isGroupToNotify(Groups grp){
        return idGroupToNotify.contains(grp);
    }

    private void removeGroupToNotify(Groups grp){
        idGroupToNotify.remove(grp);
        Integer idNotifToRemove = idNotifMessageNotification.get(grp.getId());
        if(idNotifToRemove != null)
            worker.cancelNotifById(idNotifToRemove);
    }


    private void onNewNotif(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/notifications");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notifier notif = (Notifier) dataSnapshot.getValue(Notifier.class);
                NotificationThread.this.notify(notif);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void notify(Notifier notif){
        //creation de la notification
        Intent intent = new Intent(context, MyGroupsActivity.class);
        intent.putExtra(ID_NOTIFICATION,notif.getId());

        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,REQUEST_NOTIFICATION,intent,PendingIntent.FLAG_ONE_SHOT);
        worker.make(notif.getTitle(),notif.getContent(),pendingIntent,LOGO_NOTIF,
                ID_NOTIFICATION_NOTIFIER + notif.getId(),Notification.FLAG_AUTO_CANCEL);
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
           /* CharSequence name = "channel_name";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel("default", name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notifManager.createNotificationChannel(mChannel);
            NotificationCompat.Builder buildr = new NotificationCompat.Builder(context, "default")
                    .setTicker(notif.getTitle())
                    .setSmallIcon(LOGO_NOTIF)
                    .setContentTitle(notif.getTitle())
                    .setContentText(notif.getContent())
                    .setContentIntent(pendingIntent);

            Notification notification = buildr.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notifManager.notify(ID_NOTIFICATION_NOTIFIER + notif.getId(),notification);*/
       // }else {
           /* Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis())
                    .setTicker(notif.getTitle())
                    .setSmallIcon(LOGO_NOTIF)
                    .setContentTitle(notif.getTitle())
                    .setContentText(notif.getContent())
                    .setContentIntent(pendingIntent);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notifManager.notify(ID_NOTIFICATION_NOTIFIER + notif.getId(), notification);*/
       // }
    }
    private void notifyMessage(Message msg, Groups grp){
        PendingIntent pendingIntent = getPendingIntentMessage(msg,grp);
        //notifManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        Notification notif = null;
        int idNotifMsg = 0;
        notif = worker.make(getTitleMessageNotification(grp,msg),msg.getContent(),pendingIntent,LOGO_NOTIF,
                ID_NOTIFICATION_MESSAGE+ grp.getId(),Notification.FLAG_AUTO_CANCEL);
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*NotificationChannel mChannel = getNotificationChannelMessage();
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            if(mChannel != null) {
                notifManager.createNotificationChannel(mChannel);
                NotificationCompat.Builder buildr = new NotificationCompat.Builder(context, "default")
                        .setTicker("title1")
                        .setSmallIcon(LOGO_NOTIF)
                        .setContentTitle(getTitleMessageNotification(grp, msg))
                        .setContentText(msg.getContent())
                        .setContentIntent(pendingIntent);
                notif  = buildr.build();
                setFlagsNotifMessage(notif);
                idNotifMsg = ID_NOTIFICATION_MESSAGE+ grp.getId();
                notify(idNotifMsg,notifManager,notif);*/
            //}
        //}else{
            /*Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis())
                    .setTicker("title1")
                    .setSmallIcon(LOGO_NOTIF)
                    .setContentTitle(getTitleMessageNotification(grp,msg))
                    .setContentText(msg.getContent())
                    .setContentIntent(pendingIntent);
            notif = builder.build();
            setFlagsNotifMessage(notif);
            idNotifMsg = ID_NOTIFICATION_MESSAGE+ grp.getId();
            notify(idNotifMsg,notifManager,notif);*/
        //}
        if(notif != null && idNotifMsg != 0)
            idNotifMessageNotification.put(grp.getId(),idNotifMsg);
    }

    private void notify(int id, NotificationManager manager, Notification notif){
        manager.notify(id,notif);
    }
    private void setFlagsNotifMessage(Notification notif){
        notif.flags = Notification.FLAG_AUTO_CANCEL;
    }

    private String getTitleMessageNotification(Groups grp, Message msg){
        return grp.getName() + " : "+ msg.getNameUser();
    }

    private PendingIntent getPendingIntentMessage(Message msg, Groups grp){
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ID_NOTIFICATION,msg.getId());
        intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,grp.getId()+"");
        intent.putExtra(ChatRoomActivity.EXTRA_GROUP_NAME,grp.getName());

        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context,REQUEST_NOTIFICATION,intent,PendingIntent.FLAG_ONE_SHOT);
    }

    private NotificationChannel getNotificationChannelMessage(){
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "channel_name";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            mChannel = new NotificationChannel("default", name, importance);
            mChannel.setDescription(description);
        }
        return mChannel;
    }


    public class GroupList extends ArrayList<Groups>{
        public GroupList(){
            super();
        }

        @Override
        public boolean contains(Object grp){
            if(!(grp instanceof Groups))
                return false;
            synchronized (this){
                for(Groups group : this)
                    if(group.equals(grp))
                        return true;
                return false;
            }
        }

        @Override
        public boolean add(Groups grp){
            boolean toAdd = true;
            synchronized (this){
                for(Groups group : this)
                    if(group.equals(grp))
                        toAdd = false;
                if(toAdd)
                    super.add(grp);
            }
            return true;
        }

        @Override
        public boolean remove(Object grp){
            if(!(grp instanceof Groups))
                return false;
            List<Groups> toRemove = new ArrayList<>();
            synchronized (this){
                for(Groups group : this)
                    if(group.equals(grp))
                        toRemove.add(group);
                for(Groups g : toRemove)
                    super.remove(g);
            }
            return true;
        }
    }
}
