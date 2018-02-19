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
        groups.add(new )
    }
}
