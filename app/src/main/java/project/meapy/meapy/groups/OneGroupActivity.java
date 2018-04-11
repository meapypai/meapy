package project.meapy.meapy.groups;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import project.meapy.meapy.AddDisciplineActivity;
import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.DisciplinePostsActivity;
import project.meapy.meapy.LeaveGroupActivity;
import project.meapy.meapy.MembersActivity;
import project.meapy.meapy.MyAppCompatActivity;
import project.meapy.meapy.MyApplication;
import project.meapy.meapy.NotificationThread;
import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.posts.PostAdapter;
import project.meapy.meapy.utils.CodeGroupsGenerator;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.InvitationLink;
import project.meapy.meapy.utils.firebase.PostLink;

public class OneGroupActivity extends MyAppCompatActivity {

    private TextView titleGroup;
    private TextView summaryOneGroup;
    private ImageView accedToDiscussionOneGroup;

    ListView listView;
    ArrayAdapter adapterPost;
    SubMenu subMenuDisc;
    final ArrayList<Discipline> listDiscipline = new ArrayList<>();

    private Groups group;

    private FloatingActionButton fBtn;

    public static final int LEAVE_GROUP_REQUEST = 1;

    public static final String GROUP_NAME_EXTRA = "GROUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);

        listView = findViewById(R.id.postsOneGroup);
        summaryOneGroup = findViewById(R.id.summaryOneGroup);
        titleGroup = findViewById(R.id.groupNameOneGroup);
        accedToDiscussionOneGroup = (ImageView)findViewById(R.id.accedToDiscussionOneGroup);

        final Groups grp = (Groups) getIntent().getSerializableExtra(GROUP_NAME_EXTRA);
        group = grp;

        adapterPost = new PostAdapter(OneGroupActivity.this, android.R.layout.simple_expandable_list_item_1,new ArrayList<Post>(), grp);

        String name = grp.getName();
        titleGroup.setText(name);
        summaryOneGroup.setText(grp.getSummary());

        configureToolbar();

        final Menu menu = ((NavigationView)findViewById(R.id.side_menu_one_group)).getMenu();
        subMenuDisc = menu.addSubMenu("Discipline");
        ImageButton addDisc = findViewById(R.id.addDiscOneGroup);
        addDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneGroupActivity.this, AddDisciplineActivity.class);
                intent.putExtra(AddDisciplineActivity.GROUP_EXTRA_NAME,group);
                intent.putParcelableArrayListExtra(AddDisciplineActivity.DISCS_EXTRA_NAME, listDiscipline);
                startActivity(intent);
            }
        });


        final NavigationView navigationView = findViewById(R.id.side_menu_one_group);

        findViewById(R.id.headerNavigationView).setBackground(getColorDrawable());
        findViewById(R.id.footerNavigationView).setBackground(getColorDrawable());

        listView.setAdapter(adapterPost);

        DisciplineLink.getDisciplineByGroupId(group.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                onDisciplineAdded((Discipline) getParam());
            }
        }, null);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = (Post) adapterPost.getItem(i);
                Intent intent = new Intent(OneGroupActivity.this, PostDetailsActivity.class);
                intent.putExtra(PostDetailsActivity.POST_EXTRA_NAME,post);
                intent.putExtra(PostDetailsActivity.ID_GROUP_EXTRA_NAME,group.getId()+"");
                startActivity(intent);
            }
        });

        fBtn = findViewById(R.id.sendFileOneGroup);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneGroupActivity.this, SendFileActivity.class);
                intent.putExtra(SendFileActivity.GROUP_EXTRA_NAME,grp);
                startActivity(intent);
            }
        });

        /*accedToDiscussionOneGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDiscussionAction();
            }
        });
        */

        ImageView imageView = findViewById(R.id.imageGroupOneGroup);

        //si image par défault
        if(grp.getImageName().equals(CreateGroupActivity.DEFAULT_IMAGE_GROUP)) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.group_default));
        }
        else {
            StorageReference ref = FirebaseStorage.getInstance().getReference("data_groups/" + grp.getId() +"/"+grp.getImageName());
            Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(ref).asBitmap().into(imageView);

        }
        findViewById(R.id.leaveGroupPostDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LeaveGroupActivity.class);
                startActivityForResult(i, LEAVE_GROUP_REQUEST);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //change widgets'color in terms of settings
        fBtn.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }

    private void configureToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOnegroup);
        toolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_one_group);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }
    private void onDisciplineAdded(final Discipline disc){
        listDiscipline.add(disc);
        MenuItem it  =subMenuDisc.add(disc.getName());
        it.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setChecked(true);
                Intent intent = new Intent(getApplicationContext(),DisciplinePostsActivity.class);
                intent.putExtra(DisciplinePostsActivity.GROUP_EXTRA_NAME,group);
                intent.putExtra(DisciplinePostsActivity.CURR_DISC_EXTRA_NAME,disc.getId());
                startActivity(intent);
                menuItem.setChecked(false);
                closeDrawer();
                return false;
            }
        });
        PostLink.getPostsByDiscId(disc.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                Post post = (Post) getParam();
                adapterPost.add(post);
            }
        }, new RunnableWithParam() {
            @Override
            public void run() {
                Post post = (Post) getParam();
                int idRemove = post.getId();
                deletePostById(idRemove);
            }
        }, group.getId());
    }

    private void deletePostById(int id){
        Post toDelete = null;
        for(int i = 0; i < adapterPost.getCount();i++){
            Post post =(Post) adapterPost.getItem(i);
            if(post.getId() == id){
                toDelete = post;
            }
        }
        if(toDelete != null){
            adapterPost.remove(toDelete);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LEAVE_GROUP_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                boolean result=data.getBooleanExtra("result",false);
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = null;
                if(fUser != null && result == true) {
                    uid = fUser.getUid();
                    GroupLink.leaveGroups(uid,group);
                    Toast.makeText(getApplicationContext(), getString(R.string.leave_group_toast), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MyGroupsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.onegroup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = null ;
        switch(item.getItemId()) {
            case R.id.inviteOneGroup:
                inviteAction();
                break;
            case R.id.accedToDiscussionOneGroup:
                intent = new Intent(OneGroupActivity.this, ChatRoomActivity.class);
                intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,group.getId()+"");
                intent.putExtra(ChatRoomActivity.EXTRA_GROUP_NAME,group.getName());
                break;
            case R.id.membersOneGroup:
                intent  = new Intent(this, MembersActivity.class);
                intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,group.getId()+"");
                break;
            case R.id.shareGroup:
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.invitation_code_to_group) + " " );
                intent.setType("text/plain");
                intent = Intent.createChooser(intent,"Share code");
        }
        if(intent != null) {
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void inviteAction(){
        String generatedCode = generateCodeForGroup();
        saveCodeIntoClipboard(generatedCode);
    }

    private String generateCodeForGroup(){
        String generatedCode = group.getCodeToJoin();
        if((group.getCodeToJoin() == null) || (group.getCodeToJoin().length()==0)){
            generatedCode = CodeGroupsGenerator.generate();
            group.setCodeToJoin(generatedCode);
            InvitationLink.setInviteCode(group,generatedCode);
        }
        return generatedCode;
    }

    private void saveCodeIntoClipboard(String generatedCode ) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", generatedCode);
        clipboard.setPrimaryClip(clip);
        String codeSavedClip = getString(R.string.code_saved_clip_toast);
        Toast.makeText(getApplicationContext(),codeSavedClip +" ("+generatedCode+")" ,Toast.LENGTH_LONG).show();
    }

    private void openDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_one_group);
        drawer.openDrawer(GravityCompat.START);
    }
    private void closeDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_one_group);
        drawer.closeDrawer(GravityCompat.START);
    }
}
