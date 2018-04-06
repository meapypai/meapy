package project.meapy.meapy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import project.meapy.meapy.bean.Discussion;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.database.GroupsMapper;
import project.meapy.meapy.utils.GroupsUserAdder;
import project.meapy.meapy.utils.ProviderFilePath;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.FileLink;

public class CreateGroupActivity extends MyAppCompatActivity {

    public static final String DEFAULT_IMAGE_GROUP = "default_image_group.gif";

    private static  final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final int REQUEST_ADD_USERS = 3;

    private static final int MIN_LENGTH_NAME_GROUP = 3;

    private ErrorView errorView;

    private EditText nameCreateGroup;
    private TextView imageCreateGroup;
    private EditText summuayCreateGroup;

    private ImageView insertCreateGroup;
    private ImageView addUserCreateGroup;

    private Button createNewGroupId;

    private GridView membersGridCreateGroup;
    private List<String> dataGridView = new ArrayList<>();
    private List<User> usersSelected = new ArrayList<>();
    private ArrayAdapter<String> adapterGridView;

    private String pathFile = "";
    private List<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        nameCreateGroup = (EditText)findViewById(R.id.nameCreateGroup);
        imageCreateGroup = (TextView)findViewById(R.id.imageCreateGroup);
        summuayCreateGroup = (EditText)findViewById(R.id.summuayCreateGroup);
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

        nameCreateGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {String nameGroup = nameCreateGroup.getText().toString();
                if(checkNameGroup(nameGroup)){
                    nameCreateGroup.setTextColor(Color.GREEN);
                }else{
                    nameCreateGroup.setTextColor(Color.RED);
                }
            }
        });

        createNewGroupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button)view;
                btn.setEnabled(false); //désactive le bouton pour éviter la création d'un 2eme groupe

                String summary     = summuayCreateGroup.getText().toString();
                String nameGroup = nameCreateGroup.getText().toString();

                String errorMessage = "";

                try {
                    if (checkNameGroup(nameGroup)) {
                        // START TEST INSERTION INTO DATABASE WITHOUT FILE
                        final Groups newGroup = new Groups();
                        newGroup.setName(nameGroup);
                        newGroup.setSummary(summary);

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
                            new Runnable() {
                                @Override
                                public void run() {
                                    onSucessInsertFile(null,newGroup);
                                }
                            }.run();
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

    private boolean checkNameGroup(String nameGroup){
        return nameGroup.length() >= MIN_LENGTH_NAME_GROUP;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(MyApplication.getUser() !=  null)
            Toast.makeText(getApplicationContext(),"user : "+ MyApplication.getUser().getLastName(),Toast.LENGTH_LONG).show();

        //change widgets'color in terms of settings
        insertCreateGroup.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        addUserCreateGroup.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        createNewGroupId.setBackgroundColor(colorSelectedOnSettings);
    }

    private void onSucessInsertFile(File file, Groups newGroup){
        Toast.makeText(CreateGroupActivity.this, "inserting group", Toast.LENGTH_LONG).show();
        if(file == null) {
            newGroup.setImageName(CreateGroupActivity.DEFAULT_IMAGE_GROUP);
        }
        else {
            newGroup.setImageName(file.getName());
        }
        Toast.makeText(CreateGroupActivity.this,"group created",Toast.LENGTH_LONG).show();
        //insertion of group
        GroupsMapper mapper = new GroupsMapper();
        mapper.insert(newGroup);

        //link groups with creator's user
        GroupsUserAdder.getInstance().addUserTo(newGroup);

        //link other users
        for(User u : usersList) {
            Notifier notif = new Notifier();
            notif.setContent("You've been added on group " + newGroup.getName());
            notif.setTitle("New group for you !");

            FirebaseDatabase.getInstance().getReference("users/"+u.getUid()+"/notifications/"+notif.getId()).setValue(notif);
            GroupsUserAdder.getInstance().addUserTo(u,newGroup);
        }

        //insertion of node discussion
        Discussion discussion = new Discussion();
        Message m = new Message();
        m.setNameUser("Admin");
        m.setContent("Welcolme to the group discussion");
        m.setDate(new Date());
        m.setColorNameUser("#ED2225"); //couleur par défault pour ladmin à mettre dans une classe Admin
        m.setuIdUser(UUID.randomUUID().toString());
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
                adapterGridView.clear();
                ArrayList<User> tab = data.getParcelableArrayListExtra(SearchUserActivity.EXTRA_ARRAY_USERS);
                for(int i = 0; i < tab.size(); i++) {
                    if(!usersSelected.contains(tab.get(i))) { //eviter les doublons d 'ajout
                        dataGridView.add(tab.get(i).getEmail());
                        usersList.add(tab.get(i));
                    }
                }
                adapterGridView.notifyDataSetChanged();
            }
        }
    }
}
