package project.meapy.meapy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.database.PostMapper;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.DiscussionGroupAdapter;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.ProviderFilePath;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;

public class SendFileActivity extends AppCompatActivity {

    private static  final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_LOAD_FILE = 5;
    private static final int LIMIT_DESCRIPTION_LENGTH = 20;

    private Button importFileBtnSend;
    private Button fileBtnSend;
    private Button addDiscBtn;
    private Button addGrpBtn;

    private TextView fileNameSend;

    private Spinner filesSendFile;
    private Spinner groupNameSend;
    private Spinner discTextSend;

    private EditText descTextSend;
    private EditText titleTextSend;

    private List<String> nameFiles;
    private List<File> files = new ArrayList<>();
    private ArrayAdapter<String> adapterSpinnerFiles;

    private Groups groupsProvided;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        groupsProvided = (Groups)getIntent().getSerializableExtra("GROUP");
        //buttons
        importFileBtnSend = (Button)findViewById(R.id.importFileBtnSend);
        fileBtnSend       = (Button)findViewById(R.id.fileBtnSend);
        addDiscBtn        = (Button)findViewById(R.id.addDiscSendFile);
        addGrpBtn         = (Button) findViewById(R.id.addGroupSendFile);

        //edittext
        fileNameSend      = (TextView)findViewById(R.id.fileNameSend);
        descTextSend      = (EditText)findViewById(R.id.descTextSend);
        titleTextSend     = (EditText)findViewById(R.id.titleTextSend);

        //spinners
        groupNameSend     = (Spinner)findViewById(R.id.groupNameSend);
        discTextSend      = (Spinner)findViewById(R.id.discNameSend);
        filesSendFile     = (Spinner)findViewById(R.id.filesSendFile);

        //permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }

        addGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateGroupActivity.class));
            }
        });
        //listeners
        importFileBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //intent pour récupérer un fichier
                intent.setType("*/*"); //on se limite aux images pour le moment
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Load a file"), REQUEST_LOAD_FILE);
            }
        });

        //spinner list
        nameFiles = new ArrayList<>();
        adapterSpinnerFiles = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,nameFiles);
        filesSendFile.setAdapter(adapterSpinnerFiles);


        List<Discipline> listDisc = new ArrayList<Discipline>();
        final ArrayAdapter<Discipline> dataDiscsAdapter = new ArrayAdapter<Discipline>(this,
                android.R.layout.simple_spinner_item, listDisc);
        dataDiscsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discTextSend.setAdapter(dataDiscsAdapter);
        final Map<Integer, Discipline> idToDisc = new HashMap<>();
        groupNameSend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataDiscsAdapter.clear();
                Groups grp = (Groups) groupNameSend.getSelectedItem();
                    DisciplineLink.getDisciplineByGroupId(grp.getId(), new RunnableWithParam() {
                        @Override
                        public void run() {
                            Discipline disc = (Discipline) getParam();
                            idToDisc.put(disc.getId(), disc);
                            dataDiscsAdapter.add(disc);
                        }
                    }, new RunnableWithParam() {
                        @Override
                        public void run() {
                            Discipline disc = (Discipline) getParam();
                            dataDiscsAdapter.remove(idToDisc.remove(disc.getId()));
                        }
                    });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addDiscBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddDisciplineActivity.class);
                intent.putExtra("GROUP",(Groups)groupNameSend.getSelectedItem());
                startActivity(intent);
            }
        });

        fileBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = fileNameSend.getText().toString();
                File file = new File(path);
                String description = descTextSend.getText().toString();
                final Groups group = (Groups) groupNameSend.getSelectedItem();
                final Discipline disc = (Discipline) discTextSend.getSelectedItem();
                String title = titleTextSend.getText().toString();

                //TODO : verifier que le groupe existe//
                // on force maintenant l'utilisateur a prendre des groupes existants//

                if(description.length() >= LIMIT_DESCRIPTION_LENGTH) {

                    if(disc != null && group != null && title!=null) {
                        //TODO : ajout du fichier
                        final Post post = new Post();
                        post.setTextContent(description);
                        post.setTitle(title);
                        post.setGroupId(group.getId());
                        post.setDisciplineId(disc.getId());
                        // inserer le(s) fichier(s)
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference filesRef = storage.getReference();

                        for(int i = 0; i < files.size(); i++) {
                            File f = files.get(i);
                            if(f.exists()) {
                                final Uri uriFile = Uri.fromFile(files.get(i));
                                post.setFilePath(uriFile.getLastPathSegment());
                                StorageReference groupFiles = filesRef.child("data_groups/" + group.getId() + "/" + uriFile.getLastPathSegment());
                                groupFiles.putFile(uriFile);
                            }
                        }
//                        post.setFilePath(uriFile.getLastPathSegment());
                        // inserer le lien group post dans database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        //DatabaseReference groupsDisc = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId());

                        DatabaseReference groupspost = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId()+ "/posts/").child(post.getId()+"");
                        groupspost.setValue(post);
                        Toast.makeText(SendFileActivity.this,"post added",Toast.LENGTH_LONG).show();
                        finish();
//                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                post.setFilePath(uriFile.getLastPathSegment());
//                                // inserer le lien group post dans database
//                                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                //DatabaseReference groupsDisc = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId());
//
//                                DatabaseReference groupspost = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId()+ "/posts/").child(post.getId()+"");
//                                groupspost.setValue(post);
//                                Toast.makeText(SendFileActivity.this,"post added",Toast.LENGTH_LONG).show();
//                                finish();
//                            }
//                        });
                    }else{
                        Toast.makeText(SendFileActivity.this, "you must choose groups and discipline and title", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SendFileActivity.this, "Description length must be highter than 50", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(groupsProvided == null) {
            loadingGroups();
        }else{
            final ArrayAdapter<Groups> dataGroupsAdapter = new ArrayAdapter<Groups>(this,
                    android.R.layout.simple_spinner_item, new ArrayList<Groups>());
            groupNameSend.setAdapter(dataGroupsAdapter);
            loadingGroup(dataGroupsAdapter,groupsProvided.getId());
        }
    }

    private void loadingGroups(){
        final List<Groups> listGroups = new ArrayList<Groups>();
        final ArrayAdapter<Groups> dataGroupsAdapter = new ArrayAdapter<Groups>(this,
                android.R.layout.simple_spinner_item, listGroups);
        dataGroupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSend.setAdapter(dataGroupsAdapter);

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            String uid = fUser.getUid();
            FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Integer idGroup = dataSnapshot.getValue(Integer.class);
                    synchronized (listGroups) {
                        loadingGroup(dataGroupsAdapter, idGroup);
                    }
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

    }

    private void loadingGroup(final ArrayAdapter adapter, int idGrp){
        FirebaseDatabase.getInstance().getReference("groups/"+idGrp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups added = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
                adapter.add(added);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOAD_FILE) {
            if(resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ProviderFilePath pfp = new ProviderFilePath(this);
                String path = pfp.getPathFromUri(uri);
                File file = new File(path);

                nameFiles.add(file.getName()); //ajout du nom du fichier pour l'adapter du spinner
                files.add(file); //ajout du fichier à upload
                adapterSpinnerFiles.notifyDataSetChanged();
            }
        }
    }
}
