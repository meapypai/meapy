package project.meapy.meapy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.bean.MessageNotifier;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.PostNotifier;
import project.meapy.meapy.bean.User;
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
    public static final String ID_NOTIFICATION = "id_notification";
    private static Map<Integer,Boolean> chatRoomStarted = new HashMap<>();

    private static final int LOGO_NOTIF = R.drawable.logo_app1_without_background;

    private static final int ID_NOTIFICATION_MESSAGE = 11;

    private static NotificationWorker worker ;

    private Map<Integer,Integer> idNotifMessageNotification = new HashMap<>();

    private List<Integer> idPostNotified = new ArrayList<>();
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
                        User currentUser = MyApplication.getUser();
                        Date timeRegistration = currentUser.getTimeRegistration();
                        // si ce post n'a pas ete notifie et si le post a eu lieu apres l'inscription alors notifie
                        if(fUser != null && !post.getNotifiedUser().contains(fUser.getUid())
                                && post.getDate().getTime() > timeRegistration.getTime() && !idPostNotified.contains(post.getId())
                                && !post.getUser_uid().equals(currentUser.getUid())){
                            idPostNotified.add(post.getId());
                            NotificationThread.this.notifyPost(post,grp,disc);
                        }

                    }
                },null,grp.getId());
            }
        },null);
    }

    private void onNewGroupDataReceived(){
        GroupLink.provideGroupsByCurrentuser(new RunnableWithParam() {
            @Override
            public void run() {
                final Groups grp = (Groups) getParam();
                addGroupToNotify(grp);
                onNewMessageReceived(grp);
                onNewPostReceived(grp);
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

    private void notify(Notifier notif, Intent intent){
        worker.make(notif,intent,LOGO_NOTIF,
                notif.getId(),Notification.FLAG_AUTO_CANCEL);

    }
    private void notifyMessage(Message msg, Groups grp){
        int idNotifMsg = ID_NOTIFICATION_MESSAGE+ grp.getId();
        Notifier notifier = new MessageNotifier(msg,grp,idNotifMsg);

        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ID_NOTIFICATION,msg.getId());
        intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,grp.getId()+"");
        intent.putExtra(ChatRoomActivity.EXTRA_GROUP_NAME,grp.getName());

        notify(notifier,intent);
        if(idNotifMsg != 0)
            idNotifMessageNotification.put(grp.getId(),idNotifMsg);
    }

    private void notifyPost(Post post, Groups grp, Discipline disc){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        post.getNotifiedUser().add(fUser.getUid());
        PostLink.setPostReadedByUid(post, fUser.getUid());

        Notifier notif = new PostNotifier(post,grp, context);

        Intent intent = new Intent(context, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_EXTRA_NAME, post);
        intent.putExtra(PostDetailsActivity.ID_GROUP_EXTRA_NAME, grp.getId() + "");

        notify(notif, intent);
    }


}
