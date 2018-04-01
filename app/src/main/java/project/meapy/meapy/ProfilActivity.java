package project.meapy.meapy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import project.meapy.meapy.utils.ProviderFilePath;

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ProfilActivity.REQUEST_LOAD_PROFIL_IMG) {
            if(resultCode == RESULT_OK) {
                Uri uri =  data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imgUserProfil.setImageBitmap(bitmap);
                    ProviderFilePath pfp = new ProviderFilePath(this);
                    String pathFile = pfp.getPathFromUri(uri);
                    File file = new File(pathFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
