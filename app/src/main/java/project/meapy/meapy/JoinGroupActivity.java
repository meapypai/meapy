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
                    FirebaseDatabase.getInstance().getReference("codeToGroups/"+content).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Integer idGroup = dataSnapshot.getValue(Integer.class);
                            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                            if(idGroup != null && fUser != null) {
                                String uid = fUser.getUid();
                                Toast.makeText(getApplicationContext(), "id group = " + idGroup, Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference("groups/"+idGroup+"/usersId/"+uid).setValue(uid);
                                FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/"+idGroup).setValue(idGroup.intValue());
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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
