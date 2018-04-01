package project.meapy.meapy.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import project.meapy.meapy.MyApplication;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.Comment;

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
        CommentHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_post_details_view, parent, false);
            holder = new CommentHolder();
            holder.tvAuthor  = (TextView)convertView.findViewById(R.id.authorComment);
            holder.tvContent =  (TextView)convertView.findViewById(R.id.contentComment);
            holder.imgUserComment = (ImageView)convertView.findViewById(R.id.imgUserComment);
        }
        else {
            holder = (CommentHolder) convertView.getTag();
        }

        Comment currentComment = getItem(position);
        holder.tvAuthor.setText(currentComment.getAuthorStr());
        holder.tvContent.setText(currentComment.getContent());

        if(MyApplication.getUser() != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + MyApplication.getUser().getUid() + "/" + MyApplication.getUser().getNameImageProfil());
            Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().into(holder.imgUserComment); //image à partir de la réference passée
        }


        return convertView;
    }
}
