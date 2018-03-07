package project.meapy.meapy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

import project.meapy.meapy.utils.ProviderFilePath;

public class SendFileActivity extends AppCompatActivity {

    private static  final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_LOAD_FILE = 5;

    private Button importFileBtnSend;
    private Button fileBtnSend;

    private EditText fileNameSend;
    private EditText groupNameSend;
    private EditText descTextSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        //buttons
        importFileBtnSend = (Button)findViewById(R.id.importFileBtnSend);
        fileBtnSend       = (Button)findViewById(R.id.fileBtnSend);

        //edittext
        fileNameSend      = (EditText)findViewById(R.id.fileNameSend);
        groupNameSend     = (EditText)findViewById(R.id.groupNameSend);
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
                intent.setType("image/*"); //on se limite aux images pour le moment
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Load a file"), REQUEST_LOAD_FILE);
            }
        });

        fileBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = fileNameSend.getText().toString();
                File file = new File(path);
                String description = descTextSend.getText().toString();
                String groupName = groupNameSend.getText().toString();

                if(!file.exists()) {
                    Toast.makeText(SendFileActivity.this,"File doesn't exists",Toast.LENGTH_SHORT).show();

                    //TODO : verifier que le groupe existe

                        if(description.length() >= 50) {
                            //TODO : ajout du fichier
                        }
                        else {
                            Toast.makeText(SendFileActivity.this, "Description length must be highter than 50", Toast.LENGTH_SHORT).show();
                        }
                }
                else {
                    Toast.makeText(SendFileActivity.this, "ok", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOAD_FILE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,"okkk",Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();
                ProviderFilePath pfp = new ProviderFilePath(this);
                String path = pfp.getPath(uri);
                fileNameSend.setText(path);
            }
        }
    }
}
