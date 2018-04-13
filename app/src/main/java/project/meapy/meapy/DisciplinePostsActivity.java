package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    public static final int ID_ALL_DISC = 0;

    private final List<Post> allPostList    = new ArrayList<>() ;
    private final List<Discipline> allDiscList = new ArrayList<>();

    private List<Integer> disciplinesLoaded = new ArrayList<>();
    private List<Integer> postLoaded        = new ArrayList<>();

    private Discipline currentDisc;
    private Discipline allDisc;

    private Spinner spinner;
    private EditText searchEdit;

    private ArrayAdapter<Discipline> dataDiscsAdapter;
    private ArrayAdapter<Post> adapterPost;

    private int discIdToStart;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline_posts);
        grp = (Groups) getIntent().getSerializableExtra(GROUP_EXTRA_NAME);
        allDisc = new Discipline();
        allDisc.setName("all");
        allDisc.setId(ID_ALL_DISC);

        discIdToStart = (int) getIntent().getSerializableExtra(CURR_DISC_EXTRA_NAME);

        dataDiscsAdapter = new ArrayAdapter<Discipline>(this,
                android.R.layout.simple_spinner_item, new ArrayList<Discipline>());

        spinner = findViewById(R.id.spinnerDiscPosts);
        searchEdit = findViewById(R.id.fieldSearchedDisPosts);

        dataDiscsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataDiscsAdapter);


        adapterPost = new PostAdapter(getApplicationContext(),
                R.layout.post_view_one_group,new ArrayList<Post>(), grp);
        listView = findViewById(R.id.listPostDiscPosts);
        listView.setAdapter(adapterPost);



        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searched = searchEdit.getText().toString().toLowerCase();
                adapterPost.clear();

                if(searched.length() > 0) {
                    for (Post p : allPostList) {
                        String title = p.getTitle().toLowerCase();
                        String content = p.getTextContent().toLowerCase();
                        String username = p.getUser().toLowerCase();
                        if ( (p.getDisciplineId() == currentDisc.getId() || currentDisc == allDisc) && (title.contains(searched)
                                || content.contains(searched)
                                || username.contains(searched) )) {
                            adapterPost.add(p);
                        }
                    }
                }else{
                    provideAndSetPosts(currentDisc);
                }
            }
        });

        provideDisciplines();
        configureSpinner();

    }

    private void provideDisciplines(){
        dataDiscsAdapter.add(allDisc);
        DisciplineLink.getDisciplineByGroupId(grp.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                Discipline disc = (Discipline) getParam();
                dataDiscsAdapter.add(disc);
                allDiscList.add(disc);
                if(discIdToStart == disc.getId()){
                    int idx = dataDiscsAdapter.getPosition(disc);
                    spinner.setSelection(idx);
                }

            }
        },null);
    }

    private void configureSpinner(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterPost.clear();
                Discipline disc = dataDiscsAdapter.getItem(i);
                currentDisc = disc;
                provideAndSetPosts(disc);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void provideAndSetPosts(Discipline disc){
        if(!disciplinesLoaded.contains(disc.getId()))
            disciplinesLoaded.add(disc.getId());
        if(disc != allDisc) {
            PostLink.getPostsByDiscId(disc.getId(), new RunnableWithParam() {
                @Override
                public void run() {
                    Post post = (Post) getParam();
                    adapterPost.add(post);
                    if(!postLoaded.contains(post.getId())){
                        allPostList.add(post);
                        postLoaded.add(post.getId());
                    }
                }
            }, null, grp.getId());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Post post = adapterPost.getItem(i);
                    Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.putExtra(PostDetailsActivity.POST_EXTRA_NAME, (Serializable) post);
                    startActivity(intent);
                }
            });
        }else{
            for(Discipline d : allDiscList){
                if(d!= allDisc && !disciplinesLoaded.contains(d)){
                    provideAndSetPosts(d);
                }
            }
            //adapterPost.addAll(allPostList);
        }
    }
}
