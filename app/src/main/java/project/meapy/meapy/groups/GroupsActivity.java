package project.meapy.meapy.groups;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.R;

/**
 * Created by sansaoui on 12/02/18.
 */

public class GroupsActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups);

        listView = (ListView)findViewById(R.id.groups);

        List<Group> groups = new ArrayList<>();
        groups.add(new Group(R.drawable.bdd,"Groupe 1","Je ne comprends pas..."));
        groups.add(new Group(R.drawable.maths,"Groupe 2","Il y a des erreurs"));
        groups.add(new Group(R.drawable.web,"Groupe 3","merci"));

        GroupAdapter adapter = new GroupAdapter(this,android.R.layout.simple_expandable_list_item_2,groups);

        listView.setAdapter(adapter);
    }
}
