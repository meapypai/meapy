package project.meapy.meapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import project.meapy.meapy.bean.Message;
import project.meapy.meapy.groups.DiscussionGroup;
import project.meapy.meapy.groups.discussions.DiscussionGroupsActivity;

public class ChatRoomActivity extends AppCompatActivity {

    private TextView nameGroupeChatRoom;
    private ImageButton sendChatRoom;
    private EditText messageIdChatRoom;
    private ListView scrollMessagesChat;

    private FirebaseListAdapter<Message> adapter;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        nameGroupeChatRoom = (TextView)findViewById(R.id.nameGroupeChatRoom);
        scrollMessagesChat = (ListView)findViewById(R.id.scrollMessagesChat);
        sendChatRoom       = (ImageButton)findViewById(R.id.sendChatRoom);
        messageIdChatRoom  = (EditText)findViewById(R.id.messageIdChatRoom);

        database = FirebaseDatabase.getInstance();

        Intent i = getIntent();
        String idGroup = i.getStringExtra(DiscussionGroupsActivity.EXTRA_GROUP_ID);
        String nameGroup = i.getStringExtra(DiscussionGroupsActivity.EXTRA_GROUP_NAME);

        nameGroupeChatRoom.setText(nameGroup); //set the title of the group in the activity

        final DatabaseReference ref = database.getReferenceFromUrl("https://meapy-4700d.firebaseio.com/groups/"+idGroup+"/discussions");

        adapter = new FirebaseListAdapter<Message>(this,Message.class,R.layout.message_view,ref) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView user = (TextView)v.findViewById(R.id.usernameMessage);
                TextView content = (TextView)v.findViewById(R.id.contentMessage);
                TextView date = (TextView)v.findViewById(R.id.date);

                date.setText("23h03");
                user.setText(model.getUser());
                content.setText(model.getContent());
            }
        };

        scrollMessagesChat.setAdapter(adapter);

        sendChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentMessage = messageIdChatRoom.getText().toString();

//                messageIdChatRoom.setText("");

                Message m = new Message();
                m.setContent(contentMessage);
                m.setUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                m.setDate(new Date());

                ref.push().setValue(m);
            }
        });
    }
}
