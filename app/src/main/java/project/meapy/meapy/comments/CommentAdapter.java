package project.meapy.meapy.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

import project.meapy.meapy.MyApplication;
import project.meapy.meapy.R;
import project.meapy.meapy.RegisterActivity;
import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.BuilderFormatDate;
import project.meapy.meapy.utils.RetrieveImage;

/**
 * Created by tarek on 15/03/18.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    private Context context;
    private List<Comment> list;
    public CommentAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        this.context = context;
        list=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final CommentHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_post_details_view,parent,false);
            holder = new CommentHolder();
            holder.tvAuthor  = (TextView)convertView.findViewById(R.id.authorComment);
            holder.tvContent =  (TextView)convertView.findViewById(R.id.contentComment);
            holder.imgUserComment = (ImageView)convertView.findViewById(R.id.imgUserComment);
            holder.dateComment = (TextView)convertView.findViewById(R.id.dateComment);
            convertView.setTag(holder);
        }
        else {
            holder = (CommentHolder) convertView.getTag();
        }

        final Comment currentComment = getItem(position);
//        Toast.makeText(context,currentComment.getAuthorStr(),Toast.LENGTH_SHORT).show();
        holder.tvAuthor.setText(currentComment.getAuthorStr());
        holder.tvContent.setText(currentComment.getContent());
        holder.dateComment.setText(BuilderFormatDate.getNbDayPastSinceToday(context, currentComment.getDate()));


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+currentComment.getUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = (User)dataSnapshot.getValue(User.class);

                //si image par defaut
                if(user.getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                    holder.imgUserComment.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.default_avatar));
                }
                else {
                    StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + user.getUid() + "/" + user.getNameImageProfil());
//                    Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().into(holder.imgUserComment); //image à partir de la réference passée
                    RetrieveImage.glide(ref,context,holder.imgUserComment);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return convertView;
    }
}
