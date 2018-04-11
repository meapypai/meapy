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
import project.meapy.meapy.utils.firebase.DisciplineLink;
import project.meapy.meapy.utils.firebase.PostLink;

public class DisciplinePostsActivity extends MyAppCompatActivity {
    private Groups grp;

    public static final String GROUP_EXTRA_NAME = "GROUPS";
    public static final String CURR_DISC_EXTRA_NAME = "CURRDISCIDX";
    public static final String DISCS_EXTRA_NAME = "DISCS";

    private final List<Discipline> discList = new ArrayList<>();

    private Spinner spinner;
    private ArrayAdapter<Discipline> dataDiscsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline_posts);
        grp = (Groups) getIntent().getSerializableExtra(GROUP_EXTRA_NAME);

        final int currentDiscId = (int) getIntent().getSerializableExtra(CURR_DISC_EXTRA_NAME);

        dataDiscsAdapter = new ArrayAdapter<Discipline>(this,
                android.R.layout.simple_spinner_item, discList);

        spinner = findViewById(R.id.spinnerDiscPosts);

        dataDiscsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataDiscsAdapter);
        DisciplineLink.getDisciplineByGroupId(grp.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                Discipline disc = (Discipline) getParam();
                dataDiscsAdapter.add(disc);
                if(currentDiscId == disc.getId()){
                    int idx = dataDiscsAdapter.getPosition(disc);
                    spinner.setSelection(idx);
                }

            }
        },null);

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
        final ArrayAdapter<Post> adapter = new PostAdapter(getApplicationContext(),
                R.layout.post_view_one_group,new ArrayList<Post>(), grp);
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
                intent.putExtra(PostDetailsActivity.POST_EXTRA_NAME, (Serializable) post);
                startActivity(intent);
            }
        });
    }
}
