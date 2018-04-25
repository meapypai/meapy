package project.meapy.meapy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
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
 * Created by yassi on 01/04/2018.
 */

public class ProfilActivity extends MyAppCompatActivity {

    private TextView displayNameProfil;
    private ImageView imgUserProfil;
    private FloatingActionButton btnEditProfil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil);

        displayNameProfil = (TextView)findViewById(R.id.displayNameProfil);
        imgUserProfil     = (ImageView)findViewById(R.id.imgUserProfil);
        btnEditProfil     = (FloatingActionButton)findViewById(R.id.btnEditProfil);

        displayNameProfil.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        if(MyApplication.getUser() != null) {
            if(MyApplication.getUser().getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                imgUserProfil.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_avatar));
            }
            else {
                StorageReference refImageUser = FirebaseStorage.getInstance().getReference("users_img_profil/" + MyApplication.getUser().getUid() + "/" + MyApplication.getUser().getNameImageProfil());
                RetrieveImage.glide(refImageUser,ProfilActivity.this,imgUserProfil);
            }
        }

        btnEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ModifyProfilActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        btnEditProfil.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }
}
