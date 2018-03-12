package project.meapy.meapy.groups.discussions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.DiscussionGroupAdapter;

/**
 * Created by sansaoui on 12/02/18.
 */

public class DiscussionGroupsActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discussion_groups);

        listView = (ListView)findViewById(R.id.listDiscussionGroups);

        List<DiscussionGroup> groups = new ArrayList<>();
        groups.add(new DiscussionGroup(R.drawable.bdd,"Groupe 1","Je ne comprends pas...",null));
        groups.add(new DiscussionGroup(R.drawable.maths,"Groupe 2","Il y a des erreurs",null));
        groups.add(new DiscussionGroup(R.drawable.web,"Groupe 3","merci",null));

        final DiscussionGroupAdapter adapter = new DiscussionGroupAdapter(this,android.R.layout.simple_expandable_list_item_2,groups);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(DiscussionGroupsActivity.this, ChatRoomActivity.class));
            }
        });
    }
}
