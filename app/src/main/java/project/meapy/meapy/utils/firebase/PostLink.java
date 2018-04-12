package project.meapy.meapy.utils.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.meapy.meapy.bean.Post;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class PostLink {
    public static void provideNumberOfCommentForPost(Post post, final RunnableWithParam onChange){
        String refPostStr = "groups/"+post.getGroupId()+"/disciplines/"
                +post.getDisciplineId()+"/posts/"
                +post.getId();
        DatabaseReference refComments = FirebaseDatabase.getInstance().getReference(refPostStr +"/comments");
        refComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int  n = (int) dataSnapshot.getChildrenCount();
                if(onChange != null){
                    onChange.setParam(n);
                    onChange.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void provideMarkForPost(Post post, final RunnableWithParam onChange){
        String refPostStr = "groups/"+post.getGroupId()+"/disciplines/"
                +post.getDisciplineId()+"/posts/"
                +post.getId();

        //listener positive or negative marks
        DatabaseReference refPost = FirebaseDatabase.getInstance().getReference(refPostStr);
        refPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if(onChange != null) {
                    onChange.setParam(post);
                    onChange.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getPostsByDiscId(int discId, final RunnableWithParam onPostAdded,final RunnableWithParam onPostRemoved,/*final RunnableWithParam onPostChanged,*/ int groupId){
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("groups/"+groupId+"/disciplines/"+discId+"/posts");
        postsRef
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if(onPostAdded != null) {
                            onPostAdded.setParam(post);
                            onPostAdded.run();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        /*if(onPostChanged != null){
                            onPostChanged.setParam(post);
                            onPostChanged.run();
                        }*/
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        if(onPostRemoved != null){
                            Post post = dataSnapshot.getValue(Post.class);
                            onPostRemoved.setParam(post);
                            onPostRemoved.run();
                        }
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public static void deletePost(Post post){
        FirebaseDatabase.getInstance().getReference("groups/"+post.getGroupId()+"/disciplines/"
                +post.getDisciplineId()+"/posts/"+post.getId()).removeValue();
    }
}
