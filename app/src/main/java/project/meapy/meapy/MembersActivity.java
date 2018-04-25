package project.meapy.meapy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.members.MembersAdapter;
import project.meapy.meapy.utils.SortService;

/**
 * Created by yassi on 08/04/2018.
 */

public class MembersActivity extends MyAppCompatActivity {

    private RecyclerView members_recycleview;
    private List<String> idUsers = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private MembersAdapter membersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_members);

        //extra receive from intent
        String groupId = getIntent().getStringExtra(ChatRoomActivity.EXTRA_GROUP_ID);
        String userAdminId =  getIntent().getStringExtra(OneGroupActivity.EXTRA_GROUP_USER_CREATOR);

        //adapter member
        members_recycleview = (RecyclerView)findViewById(R.id.members_recycleview);
        membersAdapter = new MembersAdapter(users, userAdminId);

        DatabaseReference idUsersRef = FirebaseDatabase.getInstance().getReference("groups/" + groupId + "/usersId");
        idUsersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String idUser = (String)dataSnapshot.getValue(String.class);
                idUsers.add(idUser);

                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+idUser);
                new Runnable() {
                    @Override
                    public void run() {
                        userRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = (User)dataSnapshot.getValue(User.class);
                                users.add(user);
                                SortService.sortMembers(users);
                                membersAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }.run();
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

        members_recycleview.setAdapter(membersAdapter);
        members_recycleview.setLayoutManager(new LinearLayoutManager(this));
    }
}
