package project.meapy.meapy.groups;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import project.meapy.meapy.R;
import project.meapy.meapy.bean.Groups;

public class OneGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_group);
        Groups grp = (Groups) getIntent().getSerializableExtra("GROUP");
        TextView titleGroup = findViewById(R.id.groupNameOneGroup);
        String name = grp.getName();
        int idAdmin = grp.getIdUserAdmin();
        int idGrp = grp.getId();
        int limituser = grp.getLimitUsers();
        titleGroup.setText(String.format("group name :%s\nid group :%d\nid admin :%d\nlimit user :%d",name,idGrp,idAdmin,limituser));

    }
}
