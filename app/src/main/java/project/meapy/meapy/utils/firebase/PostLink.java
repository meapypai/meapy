package project.meapy.meapy.utils.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Post;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class PostLink {

    public static void getPostsByDiscId(int discId, final RunnableWithParam onPostAdded,final RunnableWithParam onPostRemoved, int groupId){
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
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
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
