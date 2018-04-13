package project.meapy.meapy.posts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import project.meapy.meapy.MyApplication;
import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.members.MembersAdapter;
import project.meapy.meapy.utils.BuilderFormatDate;
import project.meapy.meapy.utils.RetrieveImage;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.DisciplineLink;
import project.meapy.meapy.utils.firebase.PostLink;
import project.meapy.meapy.utils.firebase.UserLink;

/**
 * Created by yassi on 12/04/2018.
 */

public class PostAdapter2 extends RecyclerView.Adapter {

    private List<Post> posts;
    private Context context;
    private Groups group;
    private View.OnClickListener itemClickListener;

    public PostAdapter2(List<Post> posts, Groups group,View.OnClickListener itemClickListener) {
        this.posts = posts;
        this.group = group;
        this.itemClickListener = itemClickListener;
    }

    public Post getItem(int i){
        return posts.get(i);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        context = parent.getContext();
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view_one_group,parent,false);
        if(itemClickListener != null)
            view.setOnClickListener(itemClickListener);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post p = posts.get(position);
        ((PostHolder)holder).bind(p,position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private class PostHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView nbComment;
        private TextView user;
        private TextView discipline;
        private TextView description;
        private TextView diffUpDown;
        private ImageView imgUserOneGroup;
        private TextView datePost;
        private TextView nbCommentOneGroup;
        private ImageView barLeftPost;
        private TextView nbNegMarkOneGroup;
        private TextView nbPosMarkOneGroup;

        public PostHolder(View itemView) {
            super(itemView);
            title       = (TextView)itemView.findViewById(R.id.titlePostViewOneGroup);
            discipline  = (TextView) itemView.findViewById(R.id.discPostViewOneGroup);
            description = (TextView)itemView.findViewById(R.id.descrPostViewOneGroup);
            user        = (TextView)itemView.findViewById(R.id.userPostOneGroup);
            imgUserOneGroup = (ImageView)itemView.findViewById(R.id.imgUserOneGroup);
            datePost        = (TextView)itemView.findViewById(R.id.datePost);
            nbCommentOneGroup = (TextView)itemView.findViewById(R.id.nbCommentOneGroup);
            barLeftPost = (ImageView)itemView.findViewById(R.id.barLeftPost);
            nbNegMarkOneGroup = (TextView)itemView.findViewById(R.id.nbNegMarkOneGroup);
            nbPosMarkOneGroup = (TextView)itemView.findViewById(R.id.nbPosMarkOneGroup);
        }


        public void bind(final Post post, int position) {
            title.setText(post.getTitle());
            discipline.setText(post.getDisciplineName());
            description.setText(post.getTextContent());
            user.setText(post.getUser());
            datePost.setText(BuilderFormatDate.getNbDayPastSinceToday(context,post.getDate()));

            PostLink.provideMarkForPost(post, new RunnableWithParam() {
                @Override
                public void run() {
                    Post p = (Post)getParam();
                    if(p != null) {
//                    list.set(position,p); //to update the post of the list
                        nbPosMarkOneGroup.setText(String.valueOf(p.getNbPositiveMark()));
                        nbNegMarkOneGroup.setText(String.valueOf(p.getNbNegativeMark()));
                    }
                }
            });

            PostLink.provideNumberOfCommentForPost(post, new RunnableWithParam() {
                @Override
                public void run() {
                    int  n = (int)getParam();
                    nbCommentOneGroup.setText(String.valueOf(n));
                }
            });
            //listener for number of comments

            UserLink.provideUserForPost(post, new RunnableWithParam() {
                @Override
                public void run() {
                    User user = (User) getParam();
                    //si image par defaut
                    if(user.getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                        imgUserOneGroup.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_avatar));
                    }
                    else {
                        StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + user.getUid() + "/" + user.getNameImageProfil());
                        if(!context.isRestricted())
                            RetrieveImage.glide(ref, MyApplication.getCurrentActivity(),imgUserOneGroup);
                    }
                }
            });
            //listener for change the image of the post (image profil)
            DisciplineLink.provideDisciplineColorForPost(post, new RunnableWithParam() {
                @Override
                public void run() {
                    Discipline d = (Discipline) getParam();
                    if(d.getName().equals(post.getDisciplineName())) {
                        //bar color at the left side
                        barLeftPost.setBackgroundColor(Color.parseColor(d.getColor()));
                    }
                }
            });
        }
    }

    public void sort(){
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                if(post.getDate().before(t1.getDate())){
                    return 1;
                }else if(post.getDate().after(t1.getDate())){
                    return -1;
                }else{
                    if(post.getNbPositiveMark() > t1.getNbPositiveMark()){
                        return 1;
                    }else if(post.getNbPositiveMark() < t1.getNbPositiveMark()){
                        return -1;
                    }else{
                        return 0;
                    }
                }
            }
        });
    }
}
