package project.meapy.meapy.groups;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;

public class OneGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);

        DrawerLayout drawer = findViewById(R.id.drawer_one_group);
        drawer.openDrawer(GravityCompat.START);



        Groups grp = (Groups) getIntent().getSerializableExtra("GROUP");
        TextView titleGroup = findViewById(R.id.groupNameOneGroup);
        String name = grp.getName();
        int idAdmin = grp.getIdUserAdmin();
        int idGrp = grp.getId();
        int limituser = grp.getLimitUsers();
        titleGroup.setText(String.format("group name :%s\nid group :%d\nid admin :%d\nlimit user :%d",name,idGrp,idAdmin,limituser));

        final GridView grid = findViewById(R.id.postsOneGroup);
        List<String> list = new ArrayList<String>();
        list.add("1");list.add("2");list.add("3");list.add("4");
        grid.setAdapter(new ArrayAdapter<String>(OneGroupActivity.this, android.R.layout.simple_expandable_list_item_1,list));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("groups/"+grp.getId()+"/posts/");
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                TextView tv = findViewById(R.id.testPosts);
                tv.setText(tv.getText()+" // post_id:"+post.getId());
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

        FloatingActionButton fBtn = findViewById(R.id.sendFileOneGroup);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OneGroupActivity.this, SendFileActivity.class));
            }
        });
    }
}
