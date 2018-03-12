package project.meapy.meapy.groups;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.AddDisciplineActivity;
import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;

public class OneGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);
        final Groups grp = (Groups) getIntent().getSerializableExtra("GROUP");

        // A REVOIR
        ImageButton openDrawer = findViewById(R.id.openDrawerBtn);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_one_group);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        //FIN A REVOIR
        final NavigationView navigationView = findViewById(R.id.side_menu_one_group);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        if(menuItem.getItemId() == R.id.addDiscOneGroup){
                            Toast.makeText(OneGroupActivity.this,"add groups",Toast.LENGTH_LONG).show();
                            DrawerLayout drawer = findViewById(R.id.drawer_one_group);
                            drawer.closeDrawer(GravityCompat.START);
                            Intent intent = new Intent(OneGroupActivity.this, AddDisciplineActivity.class);
                            intent.putExtra("GROUP",grp);
                            startActivity(intent);
                        }
                        return true;
                    }
                });




        TextView titleGroup = findViewById(R.id.groupNameOneGroup);
        String name = grp.getName();
        int idAdmin = grp.getIdUserAdmin();
        int idGrp = grp.getId();
        int limituser = grp.getLimitUsers();
        titleGroup.setText(String.format("group name :%s\nid group :%d\nid admin :%d\nlimit user :%d",name,idGrp,idAdmin,limituser));

        final ListView listView = findViewById(R.id.postsOneGroup);
        List<Post> list = new ArrayList<Post>();
        final ArrayAdapter adapter = new ArrayAdapter<Post>(OneGroupActivity.this, android.R.layout.simple_expandable_list_item_1,list);
        listView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("groups/"+grp.getId()+"/disciplines/");
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Discipline disc = dataSnapshot.getValue(Discipline.class);
                dataSnapshot.getRef().child("posts").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        adapter.add(post);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = (Post) adapter.getItem(i);
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
    }
}
