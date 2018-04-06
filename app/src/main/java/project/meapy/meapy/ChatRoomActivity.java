package project.meapy.meapy;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private ImageView sendChatRoom;
    private EditText messageIdChatRoom;
    private RecyclerView scrollMessagesChat;
    private ImageButton addSmileyChatRoom;

    private int smileyCpt = 0; //to display smileys layout

    private List<Message> messages = new ArrayList<>();

    private int idGroup;
    private String nameGroup;

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getWindow().setBackgroundDrawableResource(R.drawable.background_chat); //solution for the background not be resize

        scrollMessagesChat = (RecyclerView)findViewById(R.id.scrollMessagesChat);
        sendChatRoom       = (ImageView)findViewById(R.id.sendChatRoom);
        messageIdChatRoom  = (EditText)findViewById(R.id.messageIdChatRoom);
        addSmileyChatRoom  = (ImageButton) findViewById(R.id.addSmileyChatRoom);

        provideExtraData();

        setTitle(nameGroup);

        provideMessages();

        //to push the recycler view when keyboard is open
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        scrollMessagesChat.setLayoutManager(layoutManager);

        setOnSendClick();

        // ??
        sendChatRoom.setImageResource(R.drawable.ic_send_white_24dp);
        /*
        messageIdChatRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(messageIdChatRoom.getText().length() == 0) {
                    sendChatRoom.setImageResource(R.drawable.ic_send_white_24dp);
                }
                else {
                    sendChatRoom.setImageResource(R.drawable.ic_send_white_24dp);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        }); */

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
        NotificationThread.setStartedChatRoom(idGroup,true);
    }

    private void setOnSendClick(){
        sendChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentMessage = messageIdChatRoom.getText().toString();
                if(!TextUtils.isEmpty(contentMessage)) {
                    MediaPlayer mp = MediaPlayer.create(ChatRoomActivity.this,R.raw.intuition);
                    mp.start();

                    messageIdChatRoom.setText("");

                    Message m = new Message();
                    m.setContent(contentMessage);
                    m.setNameUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    m.setuIdUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    m.setDate(new Date());
                    if(MyApplication.getUser()!= null) {
                        m.setColorNameUser(MyApplication.getUser().getChatBubbleColor());
                    }
                    MessageLink.sendMessageToGroup(idGroup+"",m);

                }
            }
        });
    }
    private void provideMessages(){
        final MessagesAdapter adapter = new MessagesAdapter(messages);
        scrollMessagesChat.setLayoutManager(new LinearLayoutManager(this));
        MessageLink.getMessageByIdGroup(idGroup+"", new RunnableWithParam() {
            @Override
            public void run() {
                messages.add((Message) getParam());
                adapter.notifyDataSetChanged();
                scrollMessagesChat.scrollToPosition(messages.size() - 1);
            }
        }, new RunnableWithParam() {
            @Override
            public void run() {}
        });
        scrollMessagesChat.setAdapter(adapter);
    }
    private void provideExtraData(){
        Intent i = getIntent();
        idGroup = Integer.parseInt(i.getStringExtra(EXTRA_GROUP_ID));
        nameGroup = i.getStringExtra(EXTRA_GROUP_NAME);
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

    @Override
    protected void onStart() {
        super.onStart();
        sendChatRoom.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }

    protected void onPause(){
        super.onPause();
        NotificationThread.setStartedChatRoom(idGroup,false);
    }

    protected void onResume(){
        super.onResume();
        NotificationThread.setStartedChatRoom(idGroup,true);
    }
}
