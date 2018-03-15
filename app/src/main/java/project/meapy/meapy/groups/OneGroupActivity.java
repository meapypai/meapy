package project.meapy.meapy.groups;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.AddDisciplineActivity;
import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.DisciplinePostsActivity;
import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;
import project.meapy.meapy.posts.PostAdapter;

public class OneGroupActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private ImageView accedToDiscussionOneGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);

        final Groups grp = (Groups) getIntent().getSerializableExtra("GROUP");
        final ArrayList<Discipline> listDiscipline = new ArrayList<>();

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
        ImageButton openDrawer = findViewById(R.id.openDrawerBtn);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_one_group);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        //FIN A REVOIR
        final Menu menu = ((NavigationView)findViewById(R.id.side_menu_one_group)).getMenu();
        final SubMenu subMenuDisc = menu.addSubMenu("Discipline");
        Button addDisc = findViewById(R.id.addDiscOneGroup);
        addDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OneGroupActivity.this,"add groups",Toast.LENGTH_LONG).show();
                DrawerLayout drawer = findViewById(R.id.drawer_one_group);
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(OneGroupActivity.this, AddDisciplineActivity.class);
                intent.putExtra("GROUP",grp);
                startActivity(intent);
            }
        });


        final NavigationView navigationView = findViewById(R.id.side_menu_one_group);

        final ListView listView = findViewById(R.id.postsOneGroup);
        List<Post> list = new ArrayList<Post>();
        final ArrayAdapter adapterPost = new PostAdapter(OneGroupActivity.this, android.R.layout.simple_expandable_list_item_1,list);
        listView.setAdapter(adapterPost);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("groups/"+grp.getId()+"/disciplines/");
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Discipline disc = dataSnapshot.getValue(Discipline.class);
                listDiscipline.add(disc);
                MenuItem it  =subMenuDisc.add(disc.getName());
                it.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        Toast.makeText(getApplicationContext(),"click on "+menuItem,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),DisciplinePostsActivity.class);
                        intent.putExtra("GROUP",grp);
                        intent.putExtra("DISCS",(Serializable)listDiscipline);
                        intent.putExtra("CURRDISCIDX",listDiscipline.indexOf(disc));
                        startActivity(intent);
                        menuItem.setChecked(false);
                        return false;
                    }
                });

                dataSnapshot.getRef().child("posts").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        adapterPost.add(post);
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
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

        FloatingActionButton fBtn = findViewById(R.id.sendFileOneGroup);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OneGroupActivity.this, SendFileActivity.class));
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
    }
}
