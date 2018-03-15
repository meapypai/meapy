package project.meapy.meapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.posts.PostAdapter;

public class DisciplinePostsActivity extends AppCompatActivity {
    private Groups grp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline_posts);
        grp = (Groups) getIntent().getSerializableExtra("GROUP");
        List<Discipline> discList = (List<Discipline>) getIntent().getSerializableExtra("DISCS");
        int current = (int) getIntent().getSerializableExtra("CURRDISCIDX");

        Spinner spinner = findViewById(R.id.spinnerDiscPosts);

        final ArrayAdapter<Discipline> dataDiscsAdapter = new ArrayAdapter<Discipline>(this,
                android.R.layout.simple_spinner_item, discList);
        dataDiscsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataDiscsAdapter);
        spinner.setSelection(current);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Discipline disc = dataDiscsAdapter.getItem(i);
                providePosts(disc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void providePosts(Discipline disc){
        ListView listView = findViewById(R.id.listPostDiscPosts);
        List<Post> listPost  = new ArrayList<>();
        final ArrayAdapter<Post> adapter = new PostAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,listPost);
        listView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("groups/"+grp.getId()+"/disciplines/"+disc.getId()+"/posts").addChildEventListener(new ChildEventListener() {
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = adapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                intent.putExtra("POST",post);
                startActivity(intent);
            }
        });
    }
}
