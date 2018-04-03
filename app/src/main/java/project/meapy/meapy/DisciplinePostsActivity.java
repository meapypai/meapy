package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.posts.PostAdapter;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.PostLink;

public class DisciplinePostsActivity extends MyAppCompatActivity {
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

        PostLink.getPostsByDiscId(disc.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                adapter.add((Post) getParam());
            }
        }, null, grp.getId());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = adapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                intent.putExtra("POST", (Serializable) post);
                startActivity(intent);
            }
        });
    }
}
