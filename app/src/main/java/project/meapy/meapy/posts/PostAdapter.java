package project.meapy.meapy.posts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.database.ChildEventListener;
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
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.BuilderFormatDate;

/**
 * Created by tarek on 13/03/18.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    private static final int MAX_CHARACTERS_DESCRIPTION = 60;

    private Context context;
    private List<Post> list;
    private Groups group;

    public PostAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects, Groups grp) {
        super(context, resource, objects);
        this.context = context;
        this.group = grp;
        list=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final PostHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_view_one_group, parent, false);
            holder = new PostHolder();
            holder.title       = (TextView)convertView.findViewById(R.id.titlePostViewOneGroup);
            holder.discipline  = (TextView) convertView.findViewById(R.id.discPostViewOneGroup);
            holder.description = (TextView)convertView.findViewById(R.id.descrPostViewOneGroup);
            holder.user        = (TextView)convertView.findViewById(R.id.userPostOneGroup);
            holder.imgUserOneGroup = (ImageView)convertView.findViewById(R.id.imgUserOneGroup);
            holder.datePost        = (TextView)convertView.findViewById(R.id.datePost);
            holder.nbCommentOneGroup = (TextView)convertView.findViewById(R.id.nbCommentOneGroup);
            holder.barLeftPost = (ImageView)convertView.findViewById(R.id.barLeftPost);
            convertView.setTag(holder);
//            holder.diffUpDown  = (TextView)convertView.findViewById(R.id.diffUpDown);
        }
        else {
            holder = (PostHolder) convertView.getTag();
        }


        final Post currentPost = getItem(position);

        holder.title.setText(currentPost.getTitle());
        holder.discipline.setText(currentPost.getDisciplineName());
        holder.description.setText(slitString(currentPost.getTextContent()));
        holder.user.setText(currentPost.getUser());
        holder.datePost.setText(BuilderFormatDate.getNbDayPastSinceToday(currentPost.getDate()));

        DatabaseReference refComments = FirebaseDatabase.getInstance().getReference("groups/"+currentPost.getGroupId()+"/disciplines/"
                                                             +currentPost.getDisciplineId()+"/posts/"
                                                             +currentPost.getId()+"/comments");

        refComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int  n = (int) dataSnapshot.getChildrenCount();
                holder.nbCommentOneGroup.setText(String.valueOf(n));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+currentPost.getUser_uid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = (User)dataSnapshot.getValue(User.class);
                //si image par defaut
                if(currentPost.getNameImageUser().equals(User.DEFAULT_IMAGE_USER_NAME)) {
                    holder.imgUserOneGroup.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.default_avatar));
                }
                else {
                    StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + user.getUid() + "/" + user.getNameImageProfil());
                    Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().into(holder.imgUserOneGroup); //image à partir de la réference passée
                }

                new Runnable() {
                    @Override
                    public void run() {

                        DatabaseReference discpRef = FirebaseDatabase.getInstance().getReference("groups/" + group.getId()+ "/disciplines");
                        discpRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Discipline d = (Discipline)dataSnapshot.getValue(Discipline.class);
                                if(d.getName().equals(currentPost.getDisciplineName())) {
                                    //bar color at the left side
                                    holder.barLeftPost.setBackgroundColor(Color.parseColor(d.getColor()));
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

        return convertView;
    }

    private String slitString(String str) {
        int i = 0;
        String res = "";
        if(str.length() > 60) {
            while (i < MAX_CHARACTERS_DESCRIPTION) {
                res += str.substring(i, i + 1);
                i++;
            }
            res += "...";
        }
        else {
            res = str;
        }
        return res;
    }
}
