package project.meapy.meapy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.groups.GroupsList;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.NotificationWorker;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.MessageLink;
import project.meapy.meapy.utils.firebase.NotificationLink;
import project.meapy.meapy.utils.firebase.PostLink;

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


    private List<Groups> idGroupToNotify = new GroupsList();
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
        onNewGroupDataReceived();
        //onNewPostReceived();
    }

    private void onNewMessageReceived(final Groups grp){
        MessageLink.getMessageByIdGroup(grp.getId() + "", new RunnableWithParam() {
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
        },null);
    }

    private void onNewPostReceived(final Groups grp){
        DisciplineLink.getDisciplineByGroupId(grp.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                final Discipline disc = (Discipline) getParam();
                PostLink.getPostsByDiscId(disc.getId(), new RunnableWithParam() {
                    @Override
                    public void run() {
                        Post post = (Post) getParam();
                        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                        if(fUser != null && !post.getNotifiedUser().contains(fUser.getUid())){
                            NotificationThread.this.notifyPost(post,grp,disc);
                        }

                    }
                },null,grp.getId());
            }
        },null);
    }
    private void notifyPost(Post post, Groups grp, Discipline disc){
        Notifier notif = new Notifier();
        notif.setId(post.getId());
        notif.setTitle("New Post in "+grp.getName());
        notif.setContent(post.getTitle() + " by "+post.getUser());
        Intent intent = new Intent(context,PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_EXTRA_NAME,post);
        intent.putExtra(PostDetailsActivity.ID_GROUP_EXTRA_NAME, grp.getId());
        notify(notif, intent);
    }
    private void onNewGroupDataReceived(){
        GroupLink.provideGroupsByCurrentuser(new RunnableWithParam() {
            @Override
            public void run() {
                final Groups grp = (Groups) getParam();
                addGroupToNotify(grp);
                onNewMessageReceived(grp);
                //onNewPostReceived(grp);
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
        NotificationLink.provideNotifierByCurrentUser(new RunnableWithParam() {
            @Override
            public void run() {
                Notifier notif = (Notifier) getParam();
                Intent intent = new Intent(context, MyGroupsActivity.class);
                intent.putExtra(ID_NOTIFICATION,notif.getId());
                NotificationThread.this.notify(notif, intent);
            }
        });

    }

    private PendingIntent getPendingIntentNotifier(Notifier notif, Intent intent){
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context,REQUEST_NOTIFICATION,intent,PendingIntent.FLAG_ONE_SHOT);
    }
    private void notify(Notifier notif, Intent intent){
        PendingIntent pendingIntent = getPendingIntentNotifier(notif, intent);
        worker.make(notif.getTitle(),notif.getContent(),pendingIntent,LOGO_NOTIF,
                ID_NOTIFICATION_NOTIFIER + notif.getId(),Notification.FLAG_AUTO_CANCEL);

    }
    private void notifyMessage(Message msg, Groups grp){
        PendingIntent pendingIntent = getPendingIntentMessage(msg,grp);
        Notification notif = null;
        int idNotifMsg = 0;
        notif = worker.make(getTitleMessageNotification(grp,msg),msg.getContent(),pendingIntent,LOGO_NOTIF,
                ID_NOTIFICATION_MESSAGE+ grp.getId(),Notification.FLAG_AUTO_CANCEL);
        if(notif != null && idNotifMsg != 0)
            idNotifMessageNotification.put(grp.getId(),idNotifMsg);
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
}
