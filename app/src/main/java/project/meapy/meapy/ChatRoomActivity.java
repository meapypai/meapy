package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import project.meapy.meapy.bean.Message;
import project.meapy.meapy.chat.MessagesAdapter;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.MessageLink;

public class ChatRoomActivity extends MyAppCompatActivity {

    private TextView nameGroupeChatRoom;
    private ImageButton sendChatRoom;
    private EditText messageIdChatRoom;
    private RecyclerView scrollMessagesChat;
    private ImageButton addSmileyChatRoom;

    private int smileyCpt = 0; //to display smileys layout

    private List<Message> messages = new ArrayList<>();

    private FirebaseListAdapter<Message> adapter;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getWindow().setBackgroundDrawableResource(R.drawable.background_chat); //solution for the background not be resize

//        nameGroupeChatRoom = (TextView)findViewById(R.id.nameGroupeChatRoom);
        scrollMessagesChat = (RecyclerView)findViewById(R.id.scrollMessagesChat);
        sendChatRoom       = (ImageButton)findViewById(R.id.sendChatRoom);
        messageIdChatRoom  = (EditText)findViewById(R.id.messageIdChatRoom);
        addSmileyChatRoom  = (ImageButton) findViewById(R.id.addSmileyChatRoom);

        database = FirebaseDatabase.getInstance();

        Intent i = getIntent();
        final String idGroup = i.getStringExtra(OneGroupActivity.EXTRA_GROUP_ID);
        String nameGroup = i.getStringExtra(OneGroupActivity.EXTRA_GROUP_NAME);
        setTitle(nameGroup);

//        nameGroupeChatRoom.setText(nameGroup); //set the title of the group in the activity

        final MessagesAdapter adapter = new MessagesAdapter(messages);
        scrollMessagesChat.setLayoutManager(new LinearLayoutManager(this));
        MessageLink.getMessageByIdGroup(idGroup, new RunnableWithParam() {
            @Override
            public void run() {
                messages.add((Message) getParam());
                adapter.notifyDataSetChanged();
            }
        }, new RunnableWithParam() {
            @Override
            public void run() {}
        });


        scrollMessagesChat.setAdapter(adapter);

        sendChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentMessage = messageIdChatRoom.getText().toString();
                if(!TextUtils.isEmpty(contentMessage)) {
                    messageIdChatRoom.setText("");

                    Message m = new Message();
                    m.setContent(contentMessage);
                    m.setNameUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    m.setuIdUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    m.setDate(new Date());

                    MessageLink.sendMessageToGroup(idGroup,m);

                }
            }
        });


        messageIdChatRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(messageIdChatRoom.getText().length() == 0) {
                    sendChatRoom.setImageResource(R.drawable.ic_send_white_24dp);
                }
                else {
                    sendChatRoom.setImageResource(R.drawable.ic_send_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addSmileyChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.allSmileyChatRoom);
                if(smileyCpt == 0) {
                    layout.setVisibility(View.VISIBLE);
//                    ChatRoomActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    smileyCpt += 1;
                }
                else {
                    smileyCpt-=1;
                    layout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.allSmileyChatRoom);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(smileyCpt == 1) {
                layout.setVisibility(View.GONE);
                smileyCpt-=1;
                return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }
}
