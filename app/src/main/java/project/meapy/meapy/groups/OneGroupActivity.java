package project.meapy.meapy.groups;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.DisciplinePostsActivity;
import project.meapy.meapy.MembersActivity;
import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.posts.PostAdapter2;
import project.meapy.meapy.utils.BuilderColor;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.PostLink;

public class OneGroupActivity extends MyAppCompatActivity {

    private TextView titleGroup;
    private TextView summaryOneGroup;

    RecyclerView recyclerViewPosts;
    PostAdapter2 adapterPost;
    List<Post> posts = new ArrayList<>();
    SubMenu subMenuDisc;
    final ArrayList<Discipline> listDiscipline = new ArrayList<>();

    private Groups group;

    private FloatingActionButton fBtn;

    public static final String GROUP_NAME_EXTRA = "GROUP";
    public static final String EXTRA_GROUP_USER_CREATOR = "user_creator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);

        recyclerViewPosts = findViewById(R.id.postsOneGroup);
        summaryOneGroup = findViewById(R.id.summaryOneGroup);
        titleGroup = findViewById(R.id.groupNameOneGroup);


        final Groups grp = (Groups) getIntent().getSerializableExtra(GROUP_NAME_EXTRA);
        group = grp;

        configurePosts();
        configureSummaryGroup();
        configureToolbar();
        configureDiscMenu();
        configureColorNavigationView();
        provideDiscipline();
        configureSendFileAction();
        configureImageGroup();
        configureLeaveGroupAction();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //change widgets'color in terms of settings
        fBtn.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = null ;
        switch(item.getItemId()) {
            case R.id.searchingPosts:
                intent = new Intent(getApplicationContext(),DisciplinePostsActivity.class);
                intent.putExtra(DisciplinePostsActivity.GROUP_EXTRA_NAME,group);
                intent.putExtra(DisciplinePostsActivity.CURR_DISC_EXTRA_NAME,DisciplinePostsActivity.ID_ALL_DISC);
                break;
            case R.id.accedToDiscussionOneGroup:
                intent = new Intent(OneGroupActivity.this, ChatRoomActivity.class);
                intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,group.getId()+"");
                intent.putExtra(ChatRoomActivity.EXTRA_GROUP_NAME,group.getName());
                break;
            case R.id.membersOneGroup:
                intent  = new Intent(this, MembersActivity.class);
                intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,group.getId()+"");
                intent.putExtra(OneGroupActivity.EXTRA_GROUP_USER_CREATOR,group.getIdUserAdmin());
                break;
            case R.id.shareGroup:
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.invitation_code_to_group) + group.getCodeToJoin() );
                intent.setType("text/plain");
                intent = Intent.createChooser(intent,"Share code");
        }
        if(intent != null) {
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.onegroup_menu, menu);
        return true;
    }
    private void configureLeaveGroupAction(){
        findViewById(R.id.leaveGroupPostDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OneGroupActivity.this);
                        builder.setMessage(getString(R.string.question_leave));
                        builder.setNegativeButton(getString(R.string.no),null);
                        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onLeaveGroup();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });
    }
    private void onLeaveGroup(){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null) {
            String uid = fUser.getUid();
            GroupLink.leaveGroups(uid,group);
            Toast.makeText(getApplicationContext(), getString(R.string.leave_group_toast), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), MyGroupsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
    private void onCreateDiscipline(final EditText edit){
        String discName = edit.getText().toString();
        if (discName.length() > 0) {
            if (!nameDiscplineAlreadyExists(listDiscipline, discName)) {
                Discipline disc = new Discipline();
                disc.setName(discName);
                disc.setColor(BuilderColor.generateHexaColor());
                DisciplineLink.addDiscipline(group, disc);
                Toast.makeText(getApplicationContext(),"created",Toast.LENGTH_LONG).show();
            } else {

                edit.setText("");
                askNewDiscName();
                Toast.makeText(getApplicationContext(),"not created",Toast.LENGTH_LONG).show();
            }
        }

    }
    private boolean nameDiscplineAlreadyExists(List<Discipline> disciplines, String nameDiscipline) {
        for(Discipline d: disciplines) {
            if(d.getName().toUpperCase().equals(nameDiscipline.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    private AlertDialog dialogNewDisc;
    private void askNewDiscName(){
        final EditText nameEdit = new EditText(OneGroupActivity.this);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
             @Override
             public void run() {
                 AlertDialog.Builder builder = new AlertDialog.Builder(OneGroupActivity.this);
                 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT);
                 nameEdit.setLayoutParams(lp);
                 builder.setMessage(getString(R.string.creation_of_discipline));
                 builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         onCreateDiscipline(nameEdit);
                     }
                 });
                 builder.setView(nameEdit);
                 dialogNewDisc = builder.create();
                 dialogNewDisc.show();
                 dialogNewDisc.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                 configureOnTextChangeNewDisc(nameEdit);
             }
         }
        );
    }
    private void configureOnTextChangeNewDisc(final EditText nameEdit){
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String discName = nameEdit.getText().toString();
                if(discName.length() > 0 && !nameDiscplineAlreadyExists(listDiscipline, discName)) {
                    dialogNewDisc.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    dialogNewDisc.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }
    private void configurePosts(){
        adapterPost = new PostAdapter2(posts,group,new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = recyclerViewPosts.getChildLayoutPosition(view);
                Post post = (Post) adapterPost.getItem(position);
                Intent intent = new Intent(OneGroupActivity.this, PostDetailsActivity.class);
                intent.putExtra(OneGroupActivity.EXTRA_GROUP_USER_CREATOR,group.getIdUserAdmin());
                intent.putExtra(PostDetailsActivity.POST_EXTRA_NAME,post);
                intent.putExtra(PostDetailsActivity.ID_GROUP_EXTRA_NAME,group.getId()+"");
                startActivity(intent);
            }
        });
        recyclerViewPosts.setAdapter(adapterPost);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
    }
    private void configureSummaryGroup(){
        String name = group.getName();
        titleGroup.setText(name);
        summaryOneGroup.setText(group.getSummary());
    }
    private void configureDiscMenu(){
        final Menu menu = ((NavigationView)findViewById(R.id.side_menu_one_group)).getMenu();
        subMenuDisc = menu.addSubMenu("Discipline");
        ImageButton addDisc = findViewById(R.id.addDiscOneGroup);
        addDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askNewDiscName();
            }
        });

        findViewById(R.id.manageGroupOneGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"manage groups not yet implemented",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configureColorNavigationView(){
        findViewById(R.id.headerNavigationView).setBackground(getColorDrawable());
        findViewById(R.id.footerNavigationView).setBackground(getColorDrawable());
    }
    private void provideDiscipline(){
        DisciplineLink.getDisciplineByGroupId(group.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                onDisciplineAdded((Discipline) getParam());
            }
        }, null);
    }
    private void configureSendFileAction(){
        fBtn = findViewById(R.id.sendFileOneGroup);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneGroupActivity.this, SendFileActivity.class);
                intent.putExtra(SendFileActivity.GROUP_EXTRA_NAME,group);
                startActivity(intent);
            }
        });
    }
    private void configureImageGroup(){
        ImageView imageView = findViewById(R.id.imageGroupOneGroup);
        if(group.getImageName().equals(CreateGroupActivity.DEFAULT_IMAGE_GROUP)) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.group_default));
        }
        else {
            StorageReference ref = FirebaseStorage.getInstance().getReference("data_groups/" + group.getId() +"/"+group.getImageName());
            Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(ref).asBitmap().into(imageView);

        }
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
                return false;
            }
        });
        PostLink.getPostsByDiscId(disc.getId(), new RunnableWithParam() {
            @Override
            public void run() {
                Post post = (Post) getParam();
                posts.add(post);
                adapterPost.sort();
                adapterPost.notifyDataSetChanged();
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
        for(int i = 0; i < adapterPost.getItemCount();i++){
            Post post =(Post) posts.get(i);
            if(post.getId() == id){
                toDelete = post;
            }
        }
        if(toDelete != null){
            posts.remove(toDelete);
            adapterPost.notifyDataSetChanged();
        }
    }

    private void saveCodeIntoClipboard(String generatedCode) {
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
