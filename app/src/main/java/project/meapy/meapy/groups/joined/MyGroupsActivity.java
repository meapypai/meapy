package project.meapy.meapy.groups.joined;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.LoginActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.DiscussionGroupAdapter;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;

/**
 * Created by yassi on 23/02/2018.
 */

public class MyGroupsActivity extends AppCompatActivity {

    private FloatingActionButton createGroupId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        // listeners
        createGroupId = (FloatingActionButton)findViewById(R.id.createGroupId);
        createGroupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyGroupsActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        // providing datas
        provideGroups();
    }

    private void provideGroups(){
        List<DiscussionGroup> groups = new ArrayList<>();
        final ListView listView = findViewById(R.id.listMyGroups);
        final DiscussionGroupAdapter adapter = new DiscussionGroupAdapter(MyGroupsActivity.this,android.R.layout.simple_expandable_list_item_2,groups);
        listView.setAdapter(adapter);
        final Map<Integer,DiscussionGroup> idGroups = new HashMap<Integer, DiscussionGroup>();
        final Map<DiscussionGroup, Groups> viewToBean = new HashMap<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DiscussionGroup dGrp = adapter.getItem(i);
                Groups grp = viewToBean.get(dGrp);
                Intent intent = new Intent(MyGroupsActivity.this, OneGroupActivity.class);
                intent.putExtra("GROUP",grp);
                startActivity(intent);
            }
        });
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if(fUser != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Integer idGrp = dataSnapshot.getValue(Integer.class);
                    provideGroupById(adapter,idGroups,viewToBean,idGrp);
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

    private void provideGroupById(final DiscussionGroupAdapter adapter,
                                  final Map<Integer,DiscussionGroup> idGroups,
                                  final Map<DiscussionGroup, Groups> viewToBean, int idGroup){
        database.getReference("groups/"+idGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups added = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
                DiscussionGroup dGrp = new DiscussionGroup(R.drawable.bdd,added.getName(),added.getLimitUsers()+"", added.getId()+"/"+added.getImageName());
                idGroups.put(added.getId(),dGrp);
                viewToBean.put(dGrp,added);
                adapter.add(dGrp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


                /*.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Groups added = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
                DiscussionGroup dGrp = new DiscussionGroup(R.drawable.bdd,added.getName(),added.getLimitUsers()+"", added.getId()+"/"+added.getImageName());
                idGroups.put(added.getId(),dGrp);
                viewToBean.put(dGrp,added);
                adapter.add(dGrp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Groups changed = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Groups removed = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
                DiscussionGroup dGrp = idGroups.get(removed.getId());
                adapter.remove(dGrp);
                idGroups.remove(removed.getId());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Groups moved = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });*/
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = null ;
        switch(item.getItemId()) {
            case R.id.addFileId:
                intent = new Intent(this, SendFileActivity.class);
                break;

            case R.id.discussionsId:
                intent = new Intent(this, DiscussionGroupsActivity.class);
                break;
            case R.id.disconnect:
                intent = new Intent(this, LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                break;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
