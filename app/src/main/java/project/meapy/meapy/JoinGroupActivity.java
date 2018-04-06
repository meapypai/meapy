package project.meapy.meapy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.meapy.meapy.utils.firebase.GroupLink;

public class JoinGroupActivity extends MyAppCompatActivity {

    private Button okJoinGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        okJoinGroup = findViewById(R.id.okJoinGroup);

        okJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText codeEdit = findViewById(R.id.codeToJoinGroup);
                String content = codeEdit.getText().toString().toUpperCase();
                GroupLink.joinGroupByCode(content);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //change widgets'color in terms of settings
        okJoinGroup.setBackgroundColor(colorSelectedOnSettings);
    }
}
