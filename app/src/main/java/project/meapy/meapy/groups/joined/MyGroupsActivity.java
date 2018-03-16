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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.LoginActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.DiscussionGroupAdapter;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.GroupLink;

/**
 * Created by yassi on 23/02/2018.
 */

public class MyGroupsActivity extends AppCompatActivity {

    private FloatingActionButton createGroupId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    final Map<Integer,DiscussionGroup> idGroups = new HashMap<Integer, DiscussionGroup>();
    final Map<DiscussionGroup, Groups> viewToBean = new HashMap<>();
    ListView listView;
    DiscussionGroupAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        listView = findViewById(R.id.listMyGroups);
        adapter = new DiscussionGroupAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_2,new ArrayList<DiscussionGroup>());
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
        listView.setAdapter(adapter);
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
        GroupLink.provideGroupsByCurrentuser(new RunnableWithParam() {
            @Override
            public void run() {
                onGroupAdded((Groups) getParam());
            }
        }, new RunnableWithParam() {
            @Override
            public void run() {
                onGroupRemoved((int)getParam());
            }
        });
    }

    public void onGroupAdded(Groups added){
        if(!idGroups.containsKey(added.getId())) {
            // UPDATE UI
            DiscussionGroup dGrp = new DiscussionGroup(R.drawable.bdd, added.getName(), added.getLimitUsers() + "", added.getId() + "/" + added.getImageName());
            idGroups.put(added.getId(), dGrp);
            viewToBean.put(dGrp, added);
            adapter.add(dGrp);
        }
    }
    public void onGroupRemoved(int idGrp){
        DiscussionGroup dGrp = idGroups.remove(idGrp);
        viewToBean.remove(dGrp);
        adapter.remove(dGrp);
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
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity();
        }
        return true;
    }
}
