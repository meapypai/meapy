package project.meapy.meapy.utils.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.meapy.meapy.bean.Message;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class MessageLink {
    public static void getMessageByIdGroup(String idGroup, final RunnableWithParam onMessageAdded, final RunnableWithParam onMessageRemoved){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/discussions");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public static void getLatestMessageByGroupId(String idGroup, final RunnableWithParam onMessageAdded){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/discussions");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long count = dataSnapshot.getChildrenCount();
                ref.addChildEventListener(new ChildEventListener() {
                    int i = 0;
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(i == count) {
                            Message m = dataSnapshot.getValue(Message.class);
                            onMessageAdded.setParam(m);
                            onMessageAdded.run();
                        }
                        i++;
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void sendMessageToGroup(String idGroup, Message m){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/discussions");
        ref.push().setValue(m);
    }
}
