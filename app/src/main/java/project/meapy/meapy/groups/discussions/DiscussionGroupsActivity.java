package project.meapy.meapy.groups.discussions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.DiscussionGroupAdapter;

/**
 * Created by sansaoui on 12/02/18.
 */

public class DiscussionGroupsActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseDatabase database;
    private FirebaseListAdapter<Groups> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discussion_groups);

        listView = (ListView)findViewById(R.id.listDiscussionGroups);

        List<DiscussionGroup> groups = new ArrayList<>();

        adapter = new FirebaseListAdapter<Groups>(this,Groups.class, R.layout.group_view,FirebaseDatabase.getInstance().getReference("groups")) {
            @Override
            protected void populateView(View v, Groups model, int position) {
                ImageView image = (ImageView)v.findViewById(R.id.imgGroup);
                TextView name = (TextView)v.findViewById(R.id.nameGroup);
                TextView message = (TextView)v.findViewById(R.id.lastMessage);

                name.setText(model.getName());
                message.setText(model.getIdUserAdmin()+"");
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(DiscussionGroupsActivity.this, ChatRoomActivity.class));
            }
        });
    }
}
