package project.meapy.meapy.groups;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.AddDisciplineActivity;
import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.DisciplinePostsActivity;
import project.meapy.meapy.LeaveGroupActivity;
import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;
import project.meapy.meapy.posts.PostAdapter;
import project.meapy.meapy.utils.CodeGroupsGenerator;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;
import project.meapy.meapy.utils.firebase.PostLink;

public class OneGroupActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    ListView listView;
    ArrayAdapter adapterPost;
    SubMenu subMenuDisc;
    final ArrayList<Discipline> listDiscipline = new ArrayList<>();

    private Groups group;

    private ImageView accedToDiscussionOneGroup;

    public static final int LEAVE_GROUP_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);

        listView = findViewById(R.id.postsOneGroup);
        adapterPost = new PostAdapter(OneGroupActivity.this, android.R.layout.simple_expandable_list_item_1,new ArrayList<Post>());

        final Groups grp = (Groups) getIntent().getSerializableExtra("GROUP");
        group = grp;


        ImageView accedToDiscussionOneGroup = (ImageView)findViewById(R.id.accedToDiscussionOneGroup);

        TextView titleGroup = findViewById(R.id.groupNameOneGroup);
        String name = grp.getName();
        final int limituser = grp.getLimitUsers();
        titleGroup.setText(name);
        final TextView limitationTv = findViewById(R.id.limitOneGroup);
        limitationTv.setText(limituser + " users");
        int i = new Integer(0);
        FirebaseDatabase.getInstance().getReference("groups/"+grp.getId()+"/usersId/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long i = dataSnapshot.getChildrenCount();
                limitationTv.setText(i+"/"+limituser+" users");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // A REVOIR ( mettre une toolbar)
        final ImageButton openDrawer = findViewById(R.id.openDrawerBtn);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });
        //FIN A REVOIR
        final Menu menu = ((NavigationView)findViewById(R.id.side_menu_one_group)).getMenu();
        subMenuDisc = menu.addSubMenu("Discipline");
        ImageButton addDisc = findViewById(R.id.addDiscOneGroup);
        addDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneGroupActivity.this, AddDisciplineActivity.class);
                intent.putExtra("GROUP",grp);
                startActivity(intent);
            }
        });


        final NavigationView navigationView = findViewById(R.id.side_menu_one_group);

        listView.setAdapter(adapterPost);

        DisciplineLink.getDisciplineByGroupId(group.getId(),new RunnableWithParam(){
            @Override
            public void run() {
                onDisciplineAdded((Discipline)getParam());
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = (Post) adapterPost.getItem(i);
                Intent intent = new Intent(OneGroupActivity.this, PostDetailsActivity.class);
                intent.putExtra("POST",post);
                startActivity(intent);
            }
        });

        final FloatingActionButton fBtn = findViewById(R.id.sendFileOneGroup);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneGroupActivity.this, SendFileActivity.class);
                intent.putExtra("GROUP",grp);
                startActivity(intent);
            }
        });

        accedToDiscussionOneGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OneGroupActivity.this, ChatRoomActivity.class);
                intent.putExtra(EXTRA_GROUP_ID,grp.getId()+"");
                intent.putExtra(EXTRA_GROUP_NAME,grp.getName());
                startActivity(intent);
            }
        });

        ImageView imageView = findViewById(R.id.imageGroupOneGroup);
        StorageReference ref = FirebaseStorage.getInstance().getReference("data_groups/" + grp.getId() +"/"+grp.getImageName());
        Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(ref).asBitmap().into(imageView);

        findViewById(R.id.leaveGroupPostDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LeaveGroupActivity.class);
                startActivityForResult(i, LEAVE_GROUP_REQUEST);
            }
        });
        findViewById(R.id.inviteOneGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), CodeGroupsGenerator.generate(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onDisciplineAdded(final Discipline disc){

        listDiscipline.add(disc);
        MenuItem it  =subMenuDisc.add(disc.getName());
        it.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setChecked(true);
                Intent intent = new Intent(getApplicationContext(),DisciplinePostsActivity.class);
                intent.putExtra("GROUP",group);
                intent.putExtra("DISCS",(Serializable)listDiscipline);
                intent.putExtra("CURRDISCIDX",listDiscipline.indexOf(disc));
                startActivity(intent);
                menuItem.setChecked(false);
                openDrawer();
                return false;
            }
        });
        PostLink.getPostsByDiscId(disc.getId(),new RunnableWithParam() {
            @Override
            public void run() {
                adapterPost.add((Post)getParam());
            }
        },group.getId());
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
                    FirebaseDatabase.getInstance().getReference("users/" + uid + "/groupsId/"+group.getId()).removeValue();
                    Toast.makeText(getApplicationContext(), "leave group", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
