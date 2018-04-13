package project.meapy.meapy.utils.firebase;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class GroupLink {

    public static void provideGroupById(int idGroup, final RunnableWithParam runnable){
        FirebaseDatabase.getInstance().getReference("groups/"+idGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Groups added = dataSnapshot.getValue(Groups.class);
                runnable.setParam(added);
                runnable.run();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void provideGroupsByCurrentuser(final RunnableWithParam onGroupAdded, final RunnableWithParam onGroupRemoved){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if(fUser != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(onGroupAdded != null) {
                        Integer idGrp = dataSnapshot.getValue(Integer.class);
                        provideGroupById(idGrp, onGroupAdded);
                    }
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Integer idGrp = dataSnapshot.getValue(Integer.class);
                    if(onGroupRemoved != null) {
                        provideGroupById(idGrp, onGroupRemoved);
                        //onGroupRemoved.setParam(idGrp);
                        //onGroupRemoved.run();

                    }
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    public static void leaveGroups(String uidUser, Groups group){
        FirebaseDatabase.getInstance().getReference("users/" + uidUser + "/groupsId/"+group.getId()).removeValue();
    }

    public static void joinGroupByCode(String code){
        code = code.toUpperCase();
        FirebaseDatabase.getInstance().getReference("codeToGroups/"+code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer idGroup = dataSnapshot.getValue(Integer.class);
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if(idGroup != null && fUser != null) {
                    String uid = fUser.getUid();
                    FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/usersId/"+uid).setValue(uid);
                    FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/"+idGroup).setValue(idGroup.intValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
