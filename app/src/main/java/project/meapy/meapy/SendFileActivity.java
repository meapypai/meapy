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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class SendFileActivity extends AppCompatActivity {

    private static  final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_LOAD_FILE = 5;

    private Button importFileBtnSend;
    private Button fileBtnSend;

    private TextView fileNameSend;
    private Spinner groupNameSend;
    private EditText descTextSend;
    private Spinner discTextSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        //buttons
        importFileBtnSend = (Button)findViewById(R.id.importFileBtnSend);
        fileBtnSend       = (Button)findViewById(R.id.fileBtnSend);

        //edittext
        fileNameSend      = (TextView)findViewById(R.id.fileNameSend);
        groupNameSend     = (Spinner)findViewById(R.id.groupNameSend);
        discTextSend      = (Spinner) findViewById(R.id.discNameSend);
        descTextSend      = (EditText)findViewById(R.id.descTextSend);

        //permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }

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

        List<Discipline> listDisc = new ArrayList<Discipline>();
        final ArrayAdapter<Discipline> dataDiscsAdapter = new ArrayAdapter<Discipline>(this,
                android.R.layout.simple_spinner_item, listDisc);
        dataDiscsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discTextSend.setAdapter(dataDiscsAdapter);
        discTextSend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataDiscsAdapter.clear();
                Groups grp = (Groups) groupNameSend.getSelectedItem();
                FirebaseDatabase.getInstance().getReference("groups/"+grp.getId()+"/disciplines/").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Discipline disc = (Discipline) dataSnapshot.getValue();
                        dataDiscsAdapter.add(disc);
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
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fileBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = fileNameSend.getText().toString();
                File file = new File(path);
                String description = descTextSend.getText().toString();
                //String groupName = groupNameSend.getText().toString();
                final Groups group = (Groups) groupNameSend.getSelectedItem();
                final Discipline disc = (Discipline) discTextSend.getSelectedItem();

                if(file.exists()) {
                    //TODO : verifier que le groupe existe//
                    // on force maintenant l'utilisateur a prendre des groupes existants//

                        if(description.length() >= 50) {

                            if(disc != null && group != null) {
                                //TODO : ajout du fichier
                                final Post post = new Post();
                                post.setTextContent(description);

                                // inserer le fichier
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                final Uri uriFile = Uri.fromFile(file);
                                StorageReference filesRef = storage.getReference();

                                StorageReference groupFiles = filesRef.child("data_groups/" + group.getId() + "/" + uriFile.getLastPathSegment());
                                groupFiles.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        post.setFilePath(uriFile.getLastPathSegment());
                                        // inserer le lien group post dans database
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference groupsDisc = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId());

                                        groupsDisc.setValue(disc);

                                        DatabaseReference groupspost = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId()+ "/posts/" + post.getId());
                                        groupspost.setValue(post);
                                    }
                                });
                            }else{
                                Toast.makeText(SendFileActivity.this, "you must choose groups and discipline", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(SendFileActivity.this, "Description length must be highter than 50", Toast.LENGTH_SHORT).show();
                        }
                }
                else {
                    Toast.makeText(SendFileActivity.this,"File doesn't exists",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // loading groups
        loadingGroups();
    }

    private void loadingGroups(){
        List<Groups> listGroups = new ArrayList<Groups>();
        final ArrayAdapter<Groups> dataGroupsAdapter = new ArrayAdapter<Groups>(this,
                android.R.layout.simple_spinner_item, listGroups);
        dataGroupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSend.setAdapter(dataGroupsAdapter);

        FirebaseDatabase.getInstance().getReference("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Groups added = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
                dataGroupsAdapter.add(added);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Groups changed = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Groups removed = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Groups moved = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOAD_FILE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,"okkk",Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();
                ProviderFilePath pfp = new ProviderFilePath(this);
                String path = pfp.getPathFromUri(uri);
                fileNameSend.setText(path);
            }
        }
    }
}
