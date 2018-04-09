package project.meapy.meapy.utils.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.NotificationThread;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 06/04/18.
 */

public class NotificationLink {
    public static void removeNotificationFromCurrentUser(int idNotification){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/notifications/"+idNotification);
        ref.removeValue();
    }

    public static void provideNotifierByCurrentUser(final RunnableWithParam onAdded){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/notifications");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notifier notif = (Notifier) dataSnapshot.getValue(Notifier.class);
                if(onAdded != null){
                    onAdded.setParam(notif);
                    onAdded.run();
                }
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
}
