package project.meapy.meapy;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.utils.BuilderColor;
import project.meapy.meapy.utils.firebase.DisciplineLink;

public class AddDisciplineActivity extends AppCompatActivity {

    public static final String GROUP_EXTRA_NAME = "GROUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discipline);
        final Groups grp = (Groups) getIntent().getSerializableExtra(GROUP_EXTRA_NAME);
        Button okBtn = findViewById(R.id.okBtnAddDisc);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit = findViewById(R.id.disciplineNameAddDisc);
                String discName = edit.getText().toString();
                if(discName.length() > 0) {
//                    if(!nameDiscplineAlreadyExists(disciplines,discName)) {
                        Discipline disc = new Discipline();
                        disc.setName(discName);
                        disc.setColor(BuilderColor.generateHexaColor());
                        DisciplineLink.addDiscipline(grp,disc);
                        Toast.makeText(AddDisciplineActivity.this, getString(R.string.discipline_added_toast) + discName, Toast.LENGTH_LONG).show();
                        finish();
//                    }
//                    else {
//                        Toast.makeText(AddDisciplineActivity.this,getResources().getString(R.string.displine_already_exists),Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
    }

    private boolean nameDiscplineAlreadyExists(List<Discipline> disciplines, String nameDiscipline) {
        for(Discipline d: disciplines) {
            if(d.getName().equals(nameDiscipline.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
