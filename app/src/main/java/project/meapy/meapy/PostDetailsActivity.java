package project.meapy.meapy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.comments.CommentAdapter;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.CommentLink;

public class PostDetailsActivity extends MyAppCompatActivity {
    private Post curPost;
    private Menu menu;

    private ImageButton sendComment;
    private RelativeLayout layoutDescriptionFile;
    private EditText commentContent;
    private TextView titlePostTv;
    private TextView descFiles;
    private TextView contentPostTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        layoutDescriptionFile = (RelativeLayout)findViewById(R.id.layoutDescriptionFile);
        commentContent        = findViewById(R.id.contentCommentPostDetails);
        sendComment           = findViewById(R.id.sendCommentPostDetails);
        titlePostTv           = findViewById(R.id.titlePostDetails);
        descFiles             = findViewById(R.id.descFilesPostDetails);
        contentPostTv         = findViewById(R.id.contentPostDetails);

        final Post post = (Post) getIntent().getSerializableExtra("POST");
        curPost = post;

        contentPostTv.setText(post.getTextContent());


        titlePostTv.setText(post.getTitle());

        List<String> filesPaths = post.getFilesPaths();

        int size = filesPaths.size();
        if(size > 0) {
            String txt = filesPaths.get(0);
            if (size > 1) {
                txt += " , and " + (size - 1) + " others";
            }
            descFiles.setText(txt);
        }else{
            descFiles.setText("no file(s)");
        }

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
                    comment.setAuthorStr(fUser.getDisplayName());
                    if(MyApplication.getUser() != null) {
                        comment.setUser(MyApplication.getUser());
                    }
                    CommentLink.insertCommentToPost(comment,post);

                    commentContent.setText("");
                }
            }
        });

        commentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(commentContent.getText().length() == 0) {
                    sendComment.setImageResource(R.drawable.ic_send_white_24dp);
                }
                else {
                    sendComment.setImageResource(R.drawable.ic_send_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }

        ImageButton downloadFilesBtn = findViewById(R.id.downloadFilePostDetails);
        if(curPost.getFilesPaths().size() >= 1) {
            downloadFilesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(),"download started",Toast.LENGTH_SHORT).show();
                    downloadFile();
                }
            });
        }else{
            downloadFilesBtn.setVisibility(View.INVISIBLE);
        }


        ListView listView = findViewById(R.id.commentsPostDetails);
        List<Comment> comments = new ArrayList<>();
        final ArrayAdapter<Comment> adapter = new CommentAdapter(getApplicationContext(),
                R.layout.comment_post_details_view,comments);
        listView.setAdapter(adapter);
        CommentLink.getCommentByPost(post,new RunnableWithParam(){
            @Override
            public void run() {
                adapter.add((Comment)getParam());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        sendComment.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        layoutDescriptionFile.setBackgroundColor(colorSelectedOnSettings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.deletePostDetails){
            Toast.makeText(getApplicationContext(),"delete post",Toast.LENGTH_LONG).show();
            FirebaseDatabase.getInstance().getReference("groups/"+curPost.getGroupId()+"/disciplines/"
                    +curPost.getDisciplineId()+"/posts/"+curPost.getId()).removeValue();
            for(String filepath : curPost.getFilesPaths()){
                String refStr = "data_groups/" + curPost.getGroupId() + "/"
                        + filepath;
                FirebaseStorage.getInstance().getReference(refStr).delete();
            }
            finish();
        }
        return true;
    }

    private void downloadFile(){
        List<String> filesPaths = curPost.getFilesPaths();
        OnSuccessFailureFileDownload sucessFailureListener = new OnSuccessFailureFileDownload();
        int i = 0;
        final File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "meapy/"+curPost.getDisciplineName()+"/"+curPost.getTitle());
        dir.mkdirs();
        for (String filepath : filesPaths) {
            String[] filesPathData = filepath.split(Pattern.quote("."));
            final File localFile = new File(dir,filepath);
            String refStr = "data_groups/" + curPost.getGroupId() + "/"
                    + filepath;
            StorageReference ref = FirebaseStorage.getInstance().getReference(refStr);
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "file  download success "+dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    galleryAddPic(localFile);
                }
            })
                    .addOnFailureListener(sucessFailureListener);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            if(menu != null && curPost.getUser_uid() != null && fUser.getUid().equals(curPost.getUser_uid())){
                menu.findItem(R.id.deletePostDetails).setVisible(true);
            }
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_detail_menu, menu);
        this.menu = menu;
        return true;
    }

    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    class OnSuccessFailureFileDownload implements OnSuccessListener,OnFailureListener{
        int i = 0;
        List<File> files = new ArrayList<>();
        @Override
        public synchronized void onSuccess(Object o) {
            // local file created
            FileDownloadTask fDtask = (FileDownloadTask) o;
            Toast.makeText(getApplicationContext(), o+"", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "file "+(++i)+" download success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public synchronized void onFailure(@NonNull Exception e) {
            // failure
            Toast.makeText(getApplicationContext(), "file "+(++i)+" download fail", Toast.LENGTH_SHORT).show();
        }
    }
}
