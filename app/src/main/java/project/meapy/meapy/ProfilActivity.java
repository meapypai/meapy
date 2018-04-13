package project.meapy.meapy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.ProviderFilePath;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.FileLink;
import project.meapy.meapy.utils.firebase.UserLogined;

/**
 * Created by yassi on 01/04/2018.
 */

public class ProfilActivity extends MyAppCompatActivity {

    private static final int REQUEST_LOAD_PROFIL_IMG = 6;

    private TextView displayNameProfil;
    private ImageView imgUserProfil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil);

        displayNameProfil = (TextView)findViewById(R.id.displayNameProfil);
        imgUserProfil     = (ImageView)findViewById(R.id.imgUserProfil);

        displayNameProfil.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        imgUserProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Load an image"), REQUEST_LOAD_PROFIL_IMG);
            }
        });

        if(MyApplication.getUser() != null) {
            if(MyApplication.getUser().getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                imgUserProfil.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_avatar));
            }
            else {
                StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + MyApplication.getUser().getUid() + "/" + MyApplication.getUser().getNameImageProfil());
                Glide.with(this).using(new FirebaseImageLoader()).load(ref).asBitmap().into(imgUserProfil); //image à partir de la réference passée
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ProfilActivity.REQUEST_LOAD_PROFIL_IMG) {
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
            Glide.with(this).using(new FirebaseImageLoader()).load(ref).asBitmap().into(imgUserProfil); //image à partir de la réference passée
        }
    }
}
