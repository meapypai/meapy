package project.meapy.meapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.meapy.meapy.groups.joined.MyGroupsActivity;

public class CreateGroupActivity extends AppCompatActivity {

    private Button createNewGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        createNewGroupId = (Button)findViewById(R.id.createNewGroupId);

        createNewGroupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroupActivity.this, MyGroupsActivity.class);
                startActivity(intent);
            }
        });
    }
}
