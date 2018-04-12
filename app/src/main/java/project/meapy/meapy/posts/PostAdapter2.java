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

import java.util.List;

import project.meapy.meapy.PostDetailsActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.members.MembersAdapter;
import project.meapy.meapy.utils.RetrieveImage;

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

            String refPostStr = "groups/"+post.getGroupId()+"/disciplines/"
                    +post.getDisciplineId()+"/posts/"
                    +post.getId();

            //listener positive or negative marks
            DatabaseReference refPost = FirebaseDatabase.getInstance().getReference(refPostStr);
            refPost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post p = (Post)dataSnapshot.getValue(Post.class);
//                    list.set(position,p); //to update the post of the list
                    nbPosMarkOneGroup.setText(String.valueOf(p.getNbPositiveMark()));
                    nbNegMarkOneGroup.setText(String.valueOf(p.getNbNegativeMark()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //listener for number of comments
            DatabaseReference refComments = FirebaseDatabase.getInstance().getReference(refPostStr +"/comments");
            refComments.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int  n = (int) dataSnapshot.getChildrenCount();
                    nbCommentOneGroup.setText(String.valueOf(n));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //listener for change the image of the post (image profil)
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+post.getUser_uid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = (User)dataSnapshot.getValue(User.class);

                    //si image par defaut
                    if(user.getNameImageProfil().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                        imgUserOneGroup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.default_avatar));
                    }
                    else {
                        StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + user.getUid() + "/" + user.getNameImageProfil());
                        RetrieveImage.glide(ref,context,imgUserOneGroup);
                    }

                    new Runnable() {
                        @Override
                        public void run() {

                            DatabaseReference discpRef = FirebaseDatabase.getInstance().getReference("groups/" + group.getId()+ "/disciplines");
                            discpRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Discipline d = (Discipline)dataSnapshot.getValue(Discipline.class);
                                    if(d.getName().equals(post.getDisciplineName())) {
                                        //bar color at the left side
                                        barLeftPost.setBackgroundColor(Color.parseColor(d.getColor()));
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }.run();;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
