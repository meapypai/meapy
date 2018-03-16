package project.meapy.meapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import project.meapy.meapy.bean.Message;
import project.meapy.meapy.chat.MessagesAdapter;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;

public class ChatRoomActivity extends AppCompatActivity {

    private TextView nameGroupeChatRoom;
    private ImageButton sendChatRoom;
    private EditText messageIdChatRoom;
    private RecyclerView scrollMessagesChat;

    private List<Message> messages = new ArrayList<>();

    private FirebaseListAdapter<Message> adapter;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        nameGroupeChatRoom = (TextView)findViewById(R.id.nameGroupeChatRoom);
        scrollMessagesChat = (RecyclerView)findViewById(R.id.scrollMessagesChat);
        sendChatRoom       = (ImageButton)findViewById(R.id.sendChatRoom);
        messageIdChatRoom  = (EditText)findViewById(R.id.messageIdChatRoom);

        database = FirebaseDatabase.getInstance();

        Intent i = getIntent();
        String idGroup = i.getStringExtra(OneGroupActivity.EXTRA_GROUP_ID);
        String nameGroup = i.getStringExtra(OneGroupActivity.EXTRA_GROUP_NAME);

        nameGroupeChatRoom.setText(nameGroup); //set the title of the group in the activity

        final MessagesAdapter adapter = new MessagesAdapter(messages);
        scrollMessagesChat.setLayoutManager(new LinearLayoutManager(this));

        final DatabaseReference ref = database.getReference("groups/"+idGroup+"/discussions");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                messages.add(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        scrollMessagesChat.setAdapter(adapter);

        sendChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ChatRoomActivity.this,String.valueOf(messages.size()),Toast.LENGTH_SHORT).show();
                String contentMessage = messageIdChatRoom.getText().toString();
                if(!TextUtils.isEmpty(contentMessage)) {
                    messageIdChatRoom.setText("");

                    Message m = new Message();
                    m.setContent(contentMessage);
                    m.setUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    m.setDate(new Date());

                    ref.push().setValue(m);
                }
            }
        });
    }
}
