package project.meapy.meapy;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.comments.CommentAdapter;

public class PostDetailsActivity extends AppCompatActivity {
    private Post curPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        final Post post = (Post) getIntent().getSerializableExtra("POST");
        curPost = post;
        TextView contentPostTv = findViewById(R.id.contentPostDetails);
        contentPostTv.setText(post.getTextContent());

        TextView titlePostTv = findViewById(R.id.titlePostDetails);
        titlePostTv.setText(post.getTitle());

        final EditText commentContent = findViewById(R.id.contentCommentPostDetails);
        ImageButton sendComment = findViewById(R.id.sendCommentPostDetails);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentTxt = commentContent.getText().toString();
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if(commentTxt.length() > 0 && fUser != null) {
                    Comment comment = new Comment();
                    comment.setDate(new Date());
                    comment.setContent(commentTxt);
                    comment.setPostId(post.getId());
                    comment.setUserId(fUser.getUid());
                    FirebaseDatabase.getInstance().getReference("groups/" + post.getGroupId()
                            +"/disciplines/"+post.getDisciplineId()+ "/posts/" + post.getId()+ "/comments/"+comment.getId()).setValue(comment);
                    commentContent.setText("");
                }
            }
        });

        ListView listView = findViewById(R.id.commentsPostDetails);
        List<Comment> comments = new ArrayList<>();
        final ArrayAdapter<Comment> adapter = new CommentAdapter(getApplicationContext(),
                android.R.layout.simple_expandable_list_item_1,comments);
        listView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("groups/"+post.getGroupId()+"/disciplines/"
                +post.getDisciplineId()+"/posts/"+post.getId()+"/comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                adapter.add(comment);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.downloadFilePostDetails){
            File localFile = null;
            try {
                String filepath = curPost.getFilePath();
                String[] filesPathData = filepath.split(Pattern.quote("."));
                String prefix = filesPathData[0];
                String suffix = filesPathData[1];
                localFile = File.createTempFile(prefix, suffix);
                FirebaseStorage.getInstance().getReference("data_groups/"+ curPost.getGroupId()+"/"
                        + curPost.getFilePath()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // local file created
                        Toast.makeText(getApplicationContext(),"file downloaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failure
                        Toast.makeText(getApplicationContext(),"file download fail",Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_detail_menu, menu);
        return true;
    }
}
