package project.meapy.meapy.groups.discussions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import project.meapy.meapy.ChatRoomActivity;
import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.Groups;

/**
 * Created by sansaoui on 12/02/18.
 */

public class DiscussionGroupsActivity extends MyAppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private ListView listView;
    private FirebaseDatabase database;
    private FirebaseListAdapter<Groups> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discussion_groups);

        listView = (ListView)findViewById(R.id.listDiscussionGroups);

        adapter = new FirebaseListAdapter<Groups>(this,Groups.class, R.layout.group_view,FirebaseDatabase.getInstance().getReference("groups")) {
            @Override
            protected void populateView(View v, Groups model, int position) {
                String id = String.valueOf(model.getId());

                ImageView image = (ImageView)v.findViewById(R.id.imgGroup);
                TextView name = (TextView)v.findViewById(R.id.nameGroup);
                TextView message = (TextView)v.findViewById(R.id.summaryGroup);

                StorageReference ref = FirebaseStorage.getInstance().getReference("data_groups/" + model.getId() + "/" + model.getImageName());
                Glide.with(DiscussionGroupsActivity.this).using(new FirebaseImageLoader()).load(ref).asBitmap().into(image); //image à partir de la réference passée
                name.setText(model.getName());
                message.setText(model.getIdUserAdmin()+"");
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Groups g = adapter.getItem(i);
                Intent intent = new Intent(DiscussionGroupsActivity.this, ChatRoomActivity.class);
                intent.putExtra(EXTRA_GROUP_ID,g.getId()+"");
                intent.putExtra(EXTRA_GROUP_NAME,g.getName());
                startActivity(intent);
            }
        });
    }
}
