package project.meapy.meapy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LeaveGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_group);

        findViewById(R.id.yesLeave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLeave(true);
            }
        });

        findViewById(R.id.noLeave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLeave(false);
            }
        });
    }

    private void sendLeave(boolean leave){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",leave);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
