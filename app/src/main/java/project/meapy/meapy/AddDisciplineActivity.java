package project.meapy.meapy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;

public class AddDisciplineActivity extends MyAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discipline);
        final Groups grp = (Groups) getIntent().getSerializableExtra("GROUP");
        Button okBtn = findViewById(R.id.okBtnAddDisc);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit = findViewById(R.id.disciplineNameAddDisc);
                String discName = edit.getText().toString();
                if(discName.length() > 0) {
                    Discipline disc = new Discipline();
                    disc.setName(discName);
                    FirebaseDatabase.getInstance().getReference("groups/" + grp.getId() + "/disciplines/" + disc.getId()).setValue(disc);
                    Toast.makeText(AddDisciplineActivity.this, "discipline added " + discName, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
