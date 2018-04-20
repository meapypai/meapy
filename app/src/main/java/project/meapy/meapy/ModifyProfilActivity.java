package project.meapy.meapy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.ProviderFilePath;
import project.meapy.meapy.utils.RetrieveImage;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.FileLink;

/**
 * Created by yassi on 18/04/2018.
 */

public class ModifyProfilActivity extends MyAppCompatActivity {

    private static final int REQUEST_LOAD_PROFIL_IMG = 6;

    private EditText firstNameModifyProfil;
    private EditText lastNameModifyProfil;
    private FloatingActionButton btnValidateChangeProfil;
    private ImageView imgUserModifyProfil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_profil_activity);

        firstNameModifyProfil   = findViewById(R.id.firstNameModifyProfil);
        lastNameModifyProfil    = findViewById(R.id.lastNameModifyProfil);
        btnValidateChangeProfil = findViewById(R.id.btnValidateChangeProfil);
        imgUserModifyProfil     = findViewById(R.id.imgUserModifyProfil);

        firstNameModifyProfil.setText(MyApplication.getUser().getFirstName());
        lastNameModifyProfil.setText(MyApplication.getUser().getLastName());


        //TODO: Factorize retrieve image with Profil activity
        if(MyApplication.getUser() != null) {
            if(MyApplication.getUser().getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                imgUserModifyProfil.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_avatar));
            }
            else {
                StorageReference refImageUser = FirebaseStorage.getInstance().getReference("users_img_profil/" + MyApplication.getUser().getUid() + "/" + MyApplication.getUser().getNameImageProfil());
                RetrieveImage.glide(refImageUser,ModifyProfilActivity.this,imgUserModifyProfil);
            }
        }

        firstNameModifyProfil.addTextChangedListener(new NameTextWatcher());

        lastNameModifyProfil.addTextChangedListener(new NameTextWatcher());


        imgUserModifyProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Load an image"), REQUEST_LOAD_PROFIL_IMG);
            }
        });

        btnValidateChangeProfil.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                btnValidateChangeProfil.setBackgroundColor(getResources().getColor(R.color.messageReceiveBordeau));
                return false;
            }
        });


        btnValidateChangeProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newFirstName = firstNameModifyProfil.getText().toString();
                final String newLastName = lastNameModifyProfil.getText().toString();

//                btnValidateChangeProfil.setBackgroundColor(getResources().getColor(R.color.messageReceiveBordeau));
                if(RegisterActivity.isValidName(newFirstName)) {
                    if(RegisterActivity.isValidName(newLastName)) {
                        //TODO: put into other class
                        new Runnable() {
                            @Override
                            public void run() {
                                //change value attributes of the user in firebase
                                FirebaseDatabase.getInstance().getReference("users/" + MyApplication.getUser().getUid() + "/firstName").setValue(newFirstName);
                                FirebaseDatabase.getInstance().getReference("users/" + MyApplication.getUser().getUid() + "/lastName").setValue(newLastName);

                                //set value attributes of user loaded
                                MyApplication.getUser().setFirstName(newFirstName);
                                MyApplication.getUser().setLastName(newLastName);
                                btnValidateChangeProfil.setBackgroundColor(colorSelectedOnSettings);
                            }
                        }.run();
                    }
                    else {
                        Toast.makeText(ModifyProfilActivity.this,getResources().getString(R.string.incorrect_lastname),Toast.LENGTH_SHORT);
                    }
                }
                else {
                    Toast.makeText(ModifyProfilActivity.this,getResources().getString(R.string.incorrect_firstname),Toast.LENGTH_SHORT);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        btnValidateChangeProfil.setBackgroundColor(colorSelectedOnSettings);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ModifyProfilActivity.REQUEST_LOAD_PROFIL_IMG) {
            if(resultCode == RESULT_OK) {
                Uri uri =  data.getData();
                ProviderFilePath pfp = new ProviderFilePath(this);
                String pathFile = pfp.getPathFromUri(uri);

                final File file = new File(pathFile);

                //insertion du fichier dans storage firebase
                if(file.exists()) {
                    FileLink.insertFile(new RunnableWithParam() {
                        @Override
                        public void run() {
                            onSucessInsertFile();
                        }
                    },file);
                }
            }
        }
    }

    private void onSucessInsertFile() {
        if(MyApplication.getUser() != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + MyApplication.getUser().getUid() + "/" + MyApplication.getUser().getNameImageProfil());
            RetrieveImage.glide(ref,ModifyProfilActivity.this,imgUserModifyProfil);
        }
    }


    private class NameTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(MyApplication.getUser().getFirstName().equals(s.toString())) {
                Toast.makeText(ModifyProfilActivity.this,s,Toast.LENGTH_SHORT).show();
                btnValidateChangeProfil.setVisibility(View.GONE);
            }
            else {
                btnValidateChangeProfil.setVisibility(View.VISIBLE);
            }
        }
    }
}
