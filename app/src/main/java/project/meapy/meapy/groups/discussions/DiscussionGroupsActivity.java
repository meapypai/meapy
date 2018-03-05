package project.meapy.meapy.groups.discussions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.R;
import project.meapy.meapy.groups.Group;
import project.meapy.meapy.groups.GroupAdapter;

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

        List<Group> groups = new ArrayList<>();
        groups.add(new Group(R.drawable.bdd,"Groupe 1","Je ne comprends pas..."));
        groups.add(new Group(R.drawable.maths,"Groupe 2","Il y a des erreurs"));
        groups.add(new Group(R.drawable.web,"Groupe 3","merci"));

        GroupAdapter adapter = new GroupAdapter(this,android.R.layout.simple_expandable_list_item_2,groups);

        listView.setAdapter(adapter);
    }
}
