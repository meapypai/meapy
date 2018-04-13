package project.meapy.meapy.groups.joined;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.JoinGroupActivity;
import project.meapy.meapy.LoginActivity;
import project.meapy.meapy.MyAppCompatActivity;
import project.meapy.meapy.MyApplication;
import project.meapy.meapy.NotificationThread;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.SettingsActivity;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.NotificationLink;

/**
 * Created by yassi on 23/02/2018.
 */

public class MyGroupsActivity extends MyAppCompatActivity {

    private FloatingActionButton createGroupId;

    final Map<Integer,GroupsForView> idGroups = new HashMap<>();
    final Map<GroupsForView, Groups> viewToBean = new HashMap<>();
    ListView listView;
    GroupsAdapter adapter;

    protected boolean toolbarToConfig(){
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        listView = findViewById(R.id.listMyGroups);
        adapter = new GroupsAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_2,new ArrayList<GroupsForView>());
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
                GroupsForView dGrp = adapter.getItem(i);
                Groups grp = viewToBean.get(dGrp);
                Intent intent = new Intent(MyGroupsActivity.this, OneGroupActivity.class);
                intent.putExtra(OneGroupActivity.GROUP_NAME_EXTRA,grp);
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
                onGroupRemoved(((Groups)getParam()).getId());
            }
        });
    }

    public void onGroupAdded(Groups added){
        if(!idGroups.containsKey(added.getId())) {
            // UPDATE UI
            GroupsForView dGrp = new GroupsForView(R.drawable.bdd, added.getName(), added.getSummary() + "",added.getImageName());
            dGrp.setId(added.getId());
            idGroups.put(added.getId(), dGrp);
            viewToBean.put(dGrp, added);
            adapter.add(dGrp);
        }
    }
    public void onGroupRemoved(int idGrp){
        GroupsForView dGrp = idGroups.remove(idGrp);
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
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.disconnect:
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.joinGrpMenu:
                intent = new Intent(getApplicationContext(), JoinGroupActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        int idNotification = i.getIntExtra(NotificationThread.ID_NOTIFICATION,0);

        //suppression de la notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(idNotification);

        //supression de la notification de l'user dans la db
        NotificationLink.removeNotificationFromCurrentUser(idNotification);

        /// TEST
        // providing datas
        //provideGroups();

        /// FIN TEST
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        provideGroups();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(MyApplication.getUser() !=  null)
//            Toast.makeText(getApplicationContext(),"Bienvenue "+ MyApplication.getUser().getFirstName(),Toast.LENGTH_LONG).show();
        //to notify user if he's added to a group
        MyApplication.launch();

        //change widgets'color in terms of settings
        createGroupId.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }
}
