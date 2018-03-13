package project.meapy.meapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;

public class ChatRoomActivity extends AppCompatActivity {

    private ImageButton sendChatRoom;
    private EditText messageIdChatRoom;
    private ListView scrollMessagesChat;

    private FirebaseListAdapter<String> adapter;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        scrollMessagesChat = (ListView)findViewById(R.id.scrollMessagesChat);
        sendChatRoom       = (ImageButton)findViewById(R.id.sendChatRoom);
        messageIdChatRoom  = (EditText)findViewById(R.id.messageIdChatRoom);

        database = FirebaseDatabase.getInstance();

        Intent i = getIntent();
        String idGroup = i.getStringExtra(DiscussionGroupsActivity.EXTRA_GROUP_ID);

        Toast.makeText(ChatRoomActivity.this,idGroup,Toast.LENGTH_SHORT).show();

        final DatabaseReference ref = database.getReferenceFromUrl("https://meapy-4700d.firebaseio.com/groups/"+idGroup+"/discussions");

        adapter = new FirebaseListAdapter<String>(this, String.class, android.R.layout.simple_list_item_1,ref) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView tv  = (TextView)v;
                tv.setText(model);
            }
        };

        scrollMessagesChat.setAdapter(adapter);

        sendChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageIdChatRoom.getText().toString();

                ref.push().setValue(message);
            }
        });
    }
}
