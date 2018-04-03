package project.meapy.meapy.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;

import project.meapy.meapy.MyAppCompatActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.Message;
import project.meapy.meapy.utils.BuilderFormatDate;

/**
 * Created by yassi on 15/03/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter{
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    private List<Message> messages;
    private Context context;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message m = messages.get(position);

        if(m.getuIdUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return MESSAGE_SENT;
        }
        else {
            return MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        context = parent.getContext();

        if(viewType == MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_view,parent,false);
            return new SentMessageHolder(view);
        }
        else if(viewType == MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_receive_view,parent,false);
            return new ReceiveMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);

        switch (holder.getItemViewType()) {
            case MESSAGE_SENT:
                ((SentMessageHolder)holder).bind(m);
                break;

            case MESSAGE_RECEIVED:
                ((ReceiveMessageHolder)holder).bind(m);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView user;
        private TextView content;
        private LinearLayout bubbleSentView;

        public SentMessageHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.date);
            user = (TextView)itemView.findViewById(R.id.usernameMessage);
            content = (TextView)itemView.findViewById(R.id.contentMessage);
            bubbleSentView = (LinearLayout)itemView.findViewById(R.id.bubbleSentView);

            MyAppCompatActivity activity = (MyAppCompatActivity) context;
            bubbleSentView.setBackgroundTintList(ContextCompat.getColorStateList(activity,activity.getColorMessage().get("Sent")));
        }

        public void bind(Message message) {
            date.setText(BuilderFormatDate.formatDate(message.getDate()));
//            user.setText(message.getNameUser());
//            user.setTextColor(Color.parseColor(message.getColorNameUser()));
            content.setText(message.getContent());
        }
    }

    private class ReceiveMessageHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView user;
        private TextView content;

        public ReceiveMessageHolder(View itemView) {
            super(itemView);

            date = (TextView)itemView.findViewById(R.id.date);
            user = (TextView)itemView.findViewById(R.id.usernameMessage);
            content = (TextView)itemView.findViewById(R.id.contentMessage);

            MyAppCompatActivity activity = (MyAppCompatActivity) context;
//            content.setBackgroundTintList(ContextCompat.getColorStateList(activity,activity.getColorMessage().get("Receive")));
        }

        public void bind(Message message) {
            date.setText(BuilderFormatDate.formatDate(message.getDate()));
            user.setText(message.getNameUser());
            user.setTextColor(Color.parseColor(message.getColorNameUser()));
            content.setText(message.getContent());
        }
    }

}
