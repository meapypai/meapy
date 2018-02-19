package project.meapy.meapy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
