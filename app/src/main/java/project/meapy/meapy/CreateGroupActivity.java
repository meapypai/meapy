package project.meapy.meapy;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import project.meapy.meapy.bean.Discussion;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.database.GroupsMapper;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.GroupsUserAdder;
import project.meapy.meapy.utils.ProviderFilePath;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.FileLink;

public class CreateGroupActivity extends AppCompatActivity {

    private static  final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final int REQUEST_ADD_USERS = 3;

    private static final int MIN_LIMIT_GROUP = 20;
    private static final int MIN_LENGTH_NAME_GROUP = 3;

    private ErrorView errorView;

    private EditText nameCreateGroup;
    private TextView imageCreateGroup;
    private EditText limitCreateGroup;

    private ImageView insertCreateGroup;
    private ImageView addUserCreateGroup;

    private Button createNewGroupId;

    private GridView membersGridCreateGroup;
    private List<String> dataGridView = new ArrayList<>();
    private ArrayAdapter<String> adapterGridView;

    private String pathFile = "";
    private List<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        nameCreateGroup = (EditText)findViewById(R.id.nameCreateGroup);
        imageCreateGroup = (TextView)findViewById(R.id.imageCreateGroup);
        limitCreateGroup = (EditText)findViewById(R.id.limitCreateGroup);
        insertCreateGroup = (ImageView)findViewById(R.id.insertCreateGroup);

        createNewGroupId = (Button)findViewById(R.id.createNewGroupId);
        addUserCreateGroup = (ImageView)findViewById(R.id.addUserCreateGroup);

        membersGridCreateGroup = (GridView)findViewById(R.id.membersGridCreateGroup);

        adapterGridView = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataGridView);
        membersGridCreateGroup.setAdapter(adapterGridView);

        //permission
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }

        //listeners

        insertCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Load an image"), REQUEST_LOAD_IMAGE);
            }
        });

        createNewGroupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button)view;
                btn.setEnabled(false); //désactive le bouton pour éviter la création d'un 2eme groupe

                String limit     = limitCreateGroup.getText().toString();
                String nameGroup = nameCreateGroup.getText().toString();

                String errorMessage = "";

                try {
                    if (nameGroup.length() >= MIN_LENGTH_NAME_GROUP) {
                        int l = Integer.parseInt(limit);
                        if(l <= MIN_LIMIT_GROUP) {
                            // START TEST INSERTION INTO DATABASE WITHOUT FILE
                            final Groups newGroup = new Groups();
                            newGroup.setLimitUsers(l);
                            newGroup.setName(nameGroup);

                            // END TEST
                            final File file = new File(pathFile);
                            if(file.exists()) {
                                FileLink.insertFile(new RunnableWithParam() {
                                    @Override
                                    public void run() {
                                        onSucessInsertFile(file,newGroup);
                                    }
                                },file,newGroup);
                            }
                            else {
                                errorMessage = "File doesn't exist";
                            }
                        }
                        else {
                            errorMessage = "The limit is too highter";
                        }
                    }
                    else {
                        errorMessage = "Name groupe invalid";
                    }
                }
                catch(NumberFormatException e) {
                    errorMessage = "Limit invalid";
                }

                if(errorView == null) {
                    errorView = new ErrorView(CreateGroupActivity.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    CreateGroupActivity.this.addContentView(errorView, params);
                }
                errorView.setText(errorMessage);
                btn.setEnabled(true);
            }
        });

        addUserCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGroupActivity.this,SearchUserActivity.class);
                startActivityForResult(intent,REQUEST_ADD_USERS);
            }
        });

    }

    private void onSucessInsertFile(File file, Groups newGroup){
        final Uri uriFile = Uri.fromFile(file);
        Toast.makeText(CreateGroupActivity.this, "inserting group", Toast.LENGTH_LONG).show();
        newGroup.setImageName(uriFile.getLastPathSegment());
        Toast.makeText(CreateGroupActivity.this,"group created",Toast.LENGTH_LONG).show();
        newGroup.setImageName(file.getName());
        //insertion of group
        GroupsMapper mapper = new GroupsMapper();
        mapper.insert(newGroup);

        //link groups with creator's user
        GroupsUserAdder.getInstance().addUserTo(newGroup);

        //link other users
        for(User u : usersList) {
            GroupsUserAdder.getInstance().addUserTo(u,newGroup);
        }

        //insertion of node discussion
        Discussion discussion = new Discussion();
        Message m = new Message();
        m.setUser("Admin");
        m.setContent("Welcolme to the group discussion");
        m.setDate(new Date());
        FirebaseDatabase.getInstance().getReference("groups/"+newGroup.getId()+"/discussions/" + discussion.getId()).setValue(m);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //request concernant l'import d'une image
        if(requestCode == REQUEST_LOAD_IMAGE) {
            if(resultCode == RESULT_OK) {
                Uri uri =  data.getData();
                ProviderFilePath pfp = new ProviderFilePath(this);
                pathFile = pfp.getPathFromUri(uri);
                File file = new File(pathFile);
                imageCreateGroup.setText(file.getName());
            }
        }
        //request concernant les ajouts d'users
        else if(requestCode == REQUEST_ADD_USERS) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> tab = data.getStringArrayListExtra(SearchUserActivity.EXTRA_ARRAY_USERS);
                for(int i = 0; i < tab.size(); i++) {
                    if(!dataGridView.contains(tab.get(i))) { //eviter les doublons d 'ajout
                        dataGridView.add(tab.get(i));
                    }
                }
                adapterGridView.notifyDataSetChanged();

                //ajout des users ajouté dans le groupe
                final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
                users.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User u = (User)dataSnapshot.getValue(User.class);
                        for(String mail: dataGridView) {
                            if(mail.equals(u.getEmail())) {
                                usersList.add(u);
                            }
                        }
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
        }
    }
}
