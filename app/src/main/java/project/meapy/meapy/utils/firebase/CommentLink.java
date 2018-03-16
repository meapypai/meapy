package project.meapy.meapy.utils.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class CommentLink {

    public static void getCommentByPost(Post post, final RunnableWithParam onCommentAdded){
        FirebaseDatabase.getInstance().getReference("groups/"+post.getGroupId()+"/disciplines/"
                +post.getDisciplineId()+"/posts/"+post.getId()+"/comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                onCommentAdded.setParam(comment);
                onCommentAdded.run();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void insertCommentToPost(Comment comment, Post post){
        FirebaseDatabase.getInstance().getReference("groups/" + post.getGroupId()
                +"/disciplines/"+post.getDisciplineId()+ "/posts/" + post.getId()+ "/comments/"+comment.getId()).setValue(comment);
    }
}
