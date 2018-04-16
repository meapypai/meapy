package project.meapy.meapy.utils.firebase;

import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by yassi on 16/04/2018.
 */

public class OfficialGroupLink {

    public static void provideOfficialGroupForGroup(final RunnableWithParam onChange) {
        DatabaseReference refOfficialGroups = FirebaseDatabase.getInstance().getReference("officialGroups");
        refOfficialGroups.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int obj = (Integer)dataSnapshot.getValue(Integer.class);
                if(onChange != null){
                    onChange.setParam(obj);
                    onChange.run();
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
}
