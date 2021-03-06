package project.meapy.meapy.members;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import project.meapy.meapy.R;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.RetrieveImage;

/**
 * Created by yassi on 08/04/2018.
 */

public class MembersAdapter extends RecyclerView.Adapter {

    private List<User> users;
    private Context context;
    private String userAdminId;

    public MembersAdapter(List<User> users, String userAdminId) {
        this.users = users;
        this.userAdminId = userAdminId;
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
        holder.setIsRecyclable(false);
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

        public void bind(final User user) {
            firstnameMember.setText(user.getFirstName());
            lastnameMember.setText(user.getLastName());

            //si image par defaut
            if(user.getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                imgMember.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.default_avatar));
            }
            else {
                new Runnable() {

                    @Override
                    public void run() {
                        StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + user.getUid() + "/" + user.getNameImageProfil());
//                        Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().into(imgMember); //image à partir de la réference passée
                        RetrieveImage.glide(ref,context,imgMember);
                    }
                }.run();
            }

            //display admin image view
            if(user.getUid().equals(userAdminId)) { //s'il est admin du groupe
                adminMember.setVisibility(View.VISIBLE);
            }
        }
    }
}
