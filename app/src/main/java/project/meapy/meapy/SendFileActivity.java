package project.meapy.meapy;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import project.meapy.meapy.utils.ProviderFilePath;

public class SendFileActivity extends AppCompatActivity {

    private static final int REQUEST_LOAD_FILE = 5;

    private Button importFileBtnSend;
    private Button fileBtnSend;

    private EditText fileNameSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        //buttons
        importFileBtnSend = (Button)findViewById(R.id.importFileBtnSend);
        fileBtnSend       = (Button)findViewById(R.id.fileBtnSend);

        //edittext
        fileNameSend      = (EditText) findViewById(R.id.fileNameSend);

        importFileBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //intent pour récupérer un fichier
                intent.setType("image/*"); //on se limite aux images pour le moment
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"Load a file"), REQUEST_LOAD_FILE);
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
