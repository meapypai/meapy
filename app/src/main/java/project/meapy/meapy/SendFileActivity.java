package project.meapy.meapy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.regex.Pattern;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.utils.BuilderColor;
import project.meapy.meapy.utils.ProviderFilePath;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;

public class SendFileActivity extends MyAppCompatActivity {

    private static final double MAX_LENGTH_FILES = 20.0; //megabytes

    private static  final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_LOAD_FILE = 5;
    private static  final int REQUEST_NEW_GROUP = 8;
    private static  final int REQUEST_NEW_DISC = 9;

    private static final int LIMIT_DESCRIPTION_LENGTH = 0;
    private static final int LIMIT_TITLE_LENGTH = 5;

    private ImageButton addDiscBtn;
    private ImageButton addGrpBtn;
    private ImageButton importFileBtnSend;

    private Button fileBtnSend;
    private FloatingActionButton addPhoto;

    private TextView fileNameSend;

    private Spinner filesSendFile;
    private Spinner groupNameSend;
    private Spinner discTextSend;

    private EditText descTextSend;
    private EditText titleTextSend;

    private List<String> nameFiles;
    private List<File> files = new ArrayList<>();
    private ArrayAdapter<String> adapterSpinnerFiles;

    final List<Groups> listGroups = new ArrayList<Groups>();

    final ArrayList<Discipline> listDisc = new ArrayList<>();

    private Groups groupsProvided;

    public static final String GROUP_EXTRA_NAME = "GROUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        groupsProvided = (Groups)getIntent().getSerializableExtra(GROUP_EXTRA_NAME);
        //buttons
        importFileBtnSend = (ImageButton)findViewById(R.id.importFileBtnSend);
        addDiscBtn        = (ImageButton)findViewById(R.id.addDiscSendFile);
        addGrpBtn         = (ImageButton) findViewById(R.id.addGroupSendFile);

        fileBtnSend       = (Button)findViewById(R.id.fileBtnSend);
        addPhoto          = (FloatingActionButton) findViewById(R.id.takePhotoSendFile);

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
                startActivityForResult(new Intent(getApplicationContext(),CreateGroupActivity.class), REQUEST_NEW_GROUP);
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

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Make Photo",Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });

        //onDescTextChange();
        descTextSend.addTextChangedListener(new TextWatcher() { // TODO REVOIR
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String description = descTextSend.getText().toString();
                ImageView correct_desc = (ImageView)findViewById(R.id.correct_desc);
                if(checkDescription(description)){
                    correct_desc.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.holo_green_dark));
                }
                else {
                    correct_desc.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.white));
                }
            }
        });

        //onTitleTextChange();
        titleTextSend.addTextChangedListener(new TextWatcher() { // TODO REVOIR
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String title = titleTextSend.getText().toString();
                ImageView correct_title = (ImageView)findViewById(R.id.correct_title);
                if(checkTitle(title)){
                    correct_title.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.holo_green_dark));
                }
                else {
                    correct_title.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.white));
                }
            }
        });

        //spinner list
        nameFiles = new ArrayList<>();
        adapterSpinnerFiles = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,nameFiles);
        filesSendFile.setAdapter(adapterSpinnerFiles);


        final ArrayAdapter<Discipline> dataDiscsAdapter = new ArrayAdapter<Discipline>(this,
                android.R.layout.simple_spinner_item, listDisc);
        dataDiscsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discTextSend.setAdapter(dataDiscsAdapter);
        final Map<Integer, Discipline> idToDisc = new HashMap<>();
        findViewById(R.id.correct_group).setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.white));
        groupNameSend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataDiscsAdapter.clear();
                findViewById(R.id.correct_group).
                        setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.holo_green_dark));
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


        discTextSend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView correct_disc = (ImageView)findViewById(R.id.correct_disc);
                correct_disc.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.holo_green_dark));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addDiscBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askNewDiscName();
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


                if(isGoodLength(files)) {
                    if(checkTitle(title)) {
                        //suppression des espaces pr éviter une description n'ayant que des espaces
                        if (checkDescription(description)) { // USE STRING TRIM

                            if (disc != null && group != null && title != null) {
                                //TODO : ajout du fichier
                                final Post post = new Post();
                                post.setTextContent(description);
                                post.setTitle(title);
                                post.setGroupId(group.getId());
                                post.setDisciplineId(disc.getId());
                                post.setDisciplineName(disc.getName());
                                if (MyApplication.getUser() != null) {
                                    post.setNameImageUser(MyApplication.getUser().getNameImageProfil());
                                }
                                post.setUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                post.setDate(new Date());
                                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (fUser != null) {
                                    post.setUser_uid(fUser.getUid());
                                }
                                // inserer le(s) fichier(s)
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference filesRef = storage.getReference();
                                List<String> filesPaths = post.getFilesPaths();
                                for (int i = 0; i < files.size(); i++) {
                                    File f = files.get(i);
                                    if (f.exists()) {
                                        String suffix = f.getName().split(Pattern.quote("."))[1];
                                        String filenameDb = String.format("file_%d_%d.%s", i, post.getId(), suffix);
                                        filesPaths.add(filenameDb);
                                        StorageReference groupFiles = filesRef.child("data_groups/" + group.getId() + "/" + filenameDb);
                                        Uri uriFile = Uri.fromFile(files.get(i));
                                        groupFiles.putFile(uriFile);
                                    }
                                }
                                post.setFilesPaths(filesPaths);
                                //                        post.setFilePath(uriFile.getLastPathSegment());
                                // inserer le lien group post dans database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //DatabaseReference groupsDisc = database.getReference("groups/"+ group.getId() + "/disciplines/"+disc.getId());

                                DatabaseReference groupspost = database.getReference("groups/" + group.getId() + "/disciplines/" + disc.getId() + "/posts/").child(post.getId() + "");
                                groupspost.setValue(post);
                                Toast.makeText(SendFileActivity.this, getResources().getString(R.string.post_added), Toast.LENGTH_LONG).show();

                                finish();

                            } else {
                                Toast.makeText(SendFileActivity.this, getResources().getString(R.string.error_fields_file), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SendFileActivity.this, getResources().getString(R.string.error_description_file) + " " + LIMIT_DESCRIPTION_LENGTH + " " + getResources().getString(R.string.caracters), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(SendFileActivity.this, getResources().getString(R.string.error_title_file) + " " + LIMIT_TITLE_LENGTH + " " + getResources().getString(R.string.caracters), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SendFileActivity.this, getResources().getString(R.string.error_length_file) + " " + MAX_LENGTH_FILES + " Mo", Toast.LENGTH_SHORT).show();
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
            findViewById(R.id.addGroupSendFile).setVisibility(View.INVISIBLE);
            findViewById(R.id.groupNameSend).setEnabled(false);
        }
    }

    private AlertDialog dialogNewDisc;
    private void askNewDiscName(){
        if(groupNameSend.getCount() != 0) {
            final EditText nameEdit = new EditText(SendFileActivity.this);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                             @Override
                             public void run() {
                                 AlertDialog.Builder builder = new AlertDialog.Builder(SendFileActivity.this);
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
        }else{
            Toast.makeText(getApplicationContext(),getString(R.string.group_must_be_selected), Toast.LENGTH_LONG).show();
        }
    }
    private void onCreateDiscipline(final EditText edit){
        String discName = edit.getText().toString();
        if (discName.length() > 0) {
            if (!nameDiscplineAlreadyExists(listDisc, discName)) {
                Discipline disc = new Discipline();
                disc.setName(discName);
                disc.setColor(BuilderColor.generateHexaColor());
                DisciplineLink.addDiscipline((Groups)groupNameSend.getSelectedItem(), disc);
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
    private void configureOnTextChangeNewDisc(final EditText nameEdit){
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String discName = nameEdit.getText().toString();
                if(discName.length() > 0 && !nameDiscplineAlreadyExists(listDisc, discName)) {
                    dialogNewDisc.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    dialogNewDisc.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }


    private boolean checkDescription(String description){
        return description.replaceAll(" ", "").length() >= LIMIT_DESCRIPTION_LENGTH;
    }

    private boolean checkTitle(String title) {
        return title.replaceAll(" ", "").length() >= LIMIT_TITLE_LENGTH;
    }


    @Override
    protected void onStart() {
        super.onStart();

        //change widgets'color in terms of settings
        fileBtnSend.setBackgroundColor(colorSelectedOnSettings);
        addPhoto.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        //image buttons
        addDiscBtn.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        addGrpBtn.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        importFileBtnSend.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }

    private void loadingGroups(){
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
                    loadingGroup(dataGroupsAdapter, idGroup);
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

    private void loadingGroup(final ArrayAdapter adapter, int idGrp){
        FirebaseDatabase.getInstance().getReference("groups/"+idGrp).addValueEventListener(new ValueEventListener() {
            boolean res = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups added = dataSnapshot.getValue(Groups.class);
                // UPDATE UI
                if(!res)
                    adapter.add(added);
                res = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView correct_file = (ImageView)findViewById(R.id.correct_file);
        if(requestCode == REQUEST_LOAD_FILE) {
            if(resultCode == RESULT_OK) {
                Uri uri = data.getData();
                correct_file.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.holo_green_dark));
                addDocument(uri);
            }
        }
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                correct_file.setBackgroundTintList(ContextCompat.getColorStateList(SendFileActivity.this,android.R.color.holo_green_dark));
                addDocument(file);
            }
        }
        if(requestCode == REQUEST_NEW_GROUP){

        }
        if(requestCode == REQUEST_NEW_DISC){

        }
    }

    private void addDocument(Uri uri){
        ProviderFilePath pfp = new ProviderFilePath(this);
        String path = pfp.getPathFromUri(uri);//pfp.getPathFromUri(uri);
        File file = new File(path);
        addDocument(file);
    }

    private void addDocument(File file){
        if(file.exists()) {
            String name = file.getName();
            nameFiles.add(name); //ajout du nom du fichier pour l'adapter du spinner
            files.add(file); //ajout du fichier à upload
            adapterSpinnerFiles.notifyDataSetChanged();
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private boolean isGoodLength(List<File> files) {
        double totalLength = 0; //taille des fichiers ajoutés
        for(File f : files) {
            totalLength += f.length();
        }
        double ko = totalLength / 1024; // nb kilobytes
        double mo = ko / 2024; //nb megabytes
        return mo <= MAX_LENGTH_FILES;
    }
}
