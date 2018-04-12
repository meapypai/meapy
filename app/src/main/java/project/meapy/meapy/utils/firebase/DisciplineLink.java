package project.meapy.meapy.utils.firebase;

import android.graphics.Color;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class DisciplineLink {
    public static void provideDisciplineColorForPost(final Post post, final RunnableWithParam onAdded){
        DatabaseReference discpRef = FirebaseDatabase.getInstance().getReference("groups/" + post.getGroupId()+ "/disciplines");
        discpRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Discipline d = (Discipline)dataSnapshot.getValue(Discipline.class);
                if(onAdded != null){
                    onAdded.setParam(d);
                    onAdded.run();
                }
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

    public static void getDisciplineByGroupId(int idGroup, final RunnableWithParam onDiscAdded, final RunnableWithParam onDiscRemoved){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("groups/"+idGroup+"/disciplines/");
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(onDiscAdded != null) {
                    final Discipline disc = dataSnapshot.getValue(Discipline.class);
                    onDiscAdded.setParam(disc);
                    onDiscAdded.run();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(onDiscRemoved != null) {
                    final Discipline disc = dataSnapshot.getValue(Discipline.class);
                    onDiscRemoved.setParam(disc);
                    onDiscRemoved.run();
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void addDiscipline(Groups grp, Discipline disc){
        FirebaseDatabase.getInstance().getReference("groups/" + grp.getId() + "/disciplines/" + disc.getId()).setValue(disc);
    }
}
