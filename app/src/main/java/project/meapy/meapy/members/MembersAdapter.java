package project.meapy.meapy.members;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import project.meapy.meapy.R;
import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 08/04/2018.
 */

public class MembersAdapter extends RecyclerView.Adapter {

    private List<User> users;
    private Context context;

    public MembersAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        context = parent.getContext();

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_view,parent,false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User u = users.get(position);
        ((MemberHolder)holder).bind(u);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


//    holder

    private class MemberHolder extends RecyclerView.ViewHolder {

        private ImageView imgMember;
        private TextView firstnameMember;
        private TextView lastnameMember;
        private TextView adminMember;

        public MemberHolder(View itemView) {
            super(itemView);
            imgMember = (ImageView)itemView.findViewById(R.id.imgMember);
            firstnameMember = (TextView)itemView.findViewById(R.id.firstnameMember);
            lastnameMember = (TextView)itemView.findViewById(R.id.lastnameMember);
            adminMember = (TextView)itemView.findViewById(R.id.adminMember);
        }

        public void bind(User user) {
            firstnameMember.setText(user.getFirstName());
            lastnameMember.setText(user.getLastName());
            if(user.getRank() == 1) { //s'il est admin
                adminMember.setVisibility(View.VISIBLE);
            }
        }
    }
}
