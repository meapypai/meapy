package project.meapy.meapy.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.UserLink;

/**
 * Created by tarek on 15/03/18.
 */

public class CommentAdapter extends RecyclerView.Adapter {

    private List<Comment> comments;
    private Context context;

    public CommentAdapter(List<Comment> objects) {
        this.comments = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_post_details_view,parent,false);
        context = parent.getContext();
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        ((CommentHolder)holder).bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private class CommentHolder extends RecyclerView.ViewHolder {

        private TextView tvAuthor;
        private TextView tvContent;
        private ImageView imgUserComment;
        private TextView dateComment;
        private ImageButton menuCommentPostDetails;

        public CommentHolder(View itemView) {
            super(itemView);
            tvAuthor               = itemView.findViewById(R.id.authorComment);
            tvContent              = itemView.findViewById(R.id.contentComment);
            imgUserComment         = itemView.findViewById(R.id.imgUserComment);
            dateComment            = itemView.findViewById(R.id.dateComment);
            menuCommentPostDetails = itemView.findViewById(R.id.menuCommentPostDetails);
        }

        public void bind(Comment comment) {
            tvAuthor.setText(comment.getAuthorStr());
            tvContent.setText(comment.getContent());
            dateComment.setText(BuilderFormatDate.getNbDayPastSinceToday(context,comment.getDate()));

            UserLink.provideUserForComment(comment, new RunnableWithParam() {
                @Override
                public void run() {
                    User user = (User) getParam();
                    //si image par defaut
                    if(user.getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                        imgUserComment.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_avatar));
                    }
                    else {
                        StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + user.getUid() + "/" + user.getNameImageProfil());
                        if(!context.isRestricted())
                            RetrieveImage.glide(ref, MyApplication.getCurrentActivity(),imgUserComment);
                    }
                }
            });

            menuCommentPostDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context,menuCommentPostDetails);
                    menu.inflate(R.menu.menu_comment_post_details);

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.reportComment:
                                    break;
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });
        }
    }
}
