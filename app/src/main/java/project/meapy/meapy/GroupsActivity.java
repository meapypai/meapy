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

        //RelativeLayout rl = (RelativeLayout)findViewById(R.layout.group_view);


        List<HashMap<String , String>> data = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("nameGroup","Groupe 1");
        map1.put("lastMessage","Dernier Message...");

        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("nameGroup","Groupe 2");
        map2.put("lastMessage","Dernier Message...");

        data.add(map1); data.add(map2);

        SimpleAdapter adapter = new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,
                new String[] {"nameGroup","lastMessage"},
                new int[]{android.R.id.text1,android.R.id.text2});

        listView.setAdapter(adapter);
    }
}
