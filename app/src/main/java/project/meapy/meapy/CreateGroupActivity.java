package project.meapy.meapy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Random;

import project.meapy.meapy.bean.Discussion;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.database.GroupsMapper;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.ProviderFilePath;

public class CreateGroupActivity extends AppCompatActivity {

    private static final int REQUEST_LOAD_IMAGE = 2;

    private static final int MIN_LIMIT_GROUP = 20;
    private static final int MIN_LENGTH_NAME_GROUP = 3;

    private ErrorView errorView;

    private EditText nameCreateGroup;
    private EditText imageCreateGroup;
    private EditText limitCreateGroup;

    private Button createNewGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        nameCreateGroup = (EditText)findViewById(R.id.nameCreateGroup);
        imageCreateGroup = (EditText)findViewById(R.id.imageCreateGroup);
        limitCreateGroup = (EditText)findViewById(R.id.limitCreateGroup);

        createNewGroupId = (Button)findViewById(R.id.createNewGroupId);

        imageCreateGroup.setOnClickListener(new View.OnClickListener() {
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
                String path      = imageCreateGroup.getText().toString();
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
                            final File file = new File(path);
                            if(file.exists()) {

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                final Uri uriFile = Uri.fromFile(file);
                                StorageReference filesRef = storage.getReference();

                                StorageReference groupFiles = filesRef.child("data_groups/" + newGroup.getId() + "/" + uriFile.getLastPathSegment());
                                groupFiles.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(CreateGroupActivity.this, "inserting group", Toast.LENGTH_LONG).show();
                                        newGroup.setImageName(uriFile.getLastPathSegment());
                                        Toast.makeText(CreateGroupActivity.this,"group created",Toast.LENGTH_LONG).show();
                                        newGroup.setImageName(file.getName());
                                        //insertion of group
                                        GroupsMapper mapper = new GroupsMapper();
                                        mapper.insert(newGroup);

                                        //link groups with creator's user
                                        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = fUser.getUid();
                                        FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/"+newGroup.getId()).setValue(new Integer(newGroup.getId()));

                                        //insertion of node discussion
                                        Discussion discussion = new Discussion();
                                        FirebaseDatabase.getInstance().getReference("groups/"+newGroup.getId()+"/discussions/" + discussion.getId()).setValue("");

                                        finish();
                                        Intent intent = new Intent(CreateGroupActivity.this, MyGroupsActivity.class);
                                        startActivity(intent);
                                    }
                                });

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
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOAD_IMAGE) {
            if(resultCode == RESULT_OK) {
                Uri uri =  data.getData();
                ProviderFilePath pfp = new ProviderFilePath(this);
                String path = pfp.getPathFromUri(uri);
                imageCreateGroup.setText(path);
            }
        }
    }
}
