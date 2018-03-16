package project.meapy.meapy.utils.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Message;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class MessageLink {
    public static void getMessageByIdGroup(String idGroup, final RunnableWithParam onMessageAdded, final RunnableWithParam onMessageRemoved){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/discussions");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                onMessageAdded.setParam(m);
                onMessageAdded.run();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message m = dataSnapshot.getValue(Message.class);
                onMessageRemoved.setParam(m);
                onMessageRemoved.run();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void sendMessageToGroup(String idGroup, Message m){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/discussions");
        ref.push().setValue(m);
    }
}
