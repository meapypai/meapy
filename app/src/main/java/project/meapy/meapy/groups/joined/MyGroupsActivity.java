package project.meapy.meapy.groups.joined;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.database.GroupsMapper;
import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;

/**
 * Created by yassi on 23/02/2018.
 */

public class MyGroupsActivity extends AppCompatActivity {

    private FloatingActionButton createGroupId;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        createGroupId = (FloatingActionButton)findViewById(R.id.createGroupId);

        createGroupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyGroupsActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        DatabaseReference groupsRef = database.getReference("groups");
        groupsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Groups added = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
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
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Groups moved = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


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
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
