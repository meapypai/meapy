package project.meapy.meapy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.comments.CommentAdapter;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.CommentLink;
import project.meapy.meapy.utils.firebase.FileLink;
import project.meapy.meapy.utils.firebase.PostLink;

public class PostDetailsActivity extends MyAppCompatActivity implements RewardedVideoAdListener{

    public static final String SAMPLE_APMOB_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String SAMPLE_APMOB_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private RewardedVideoAd rewardedVideoAd;

    public static final int MIN_COINS_TO_DOWNLOAD_FILE = 1;
    public static final int COINS_REMOVED_ON_DOWNLOADED_FILE = 1;

    private Post curPost;
    private Menu menu;

    private ImageButton downPostDetails;
    private ImageButton upPostDetails;
    private ImageButton sendComment;
    private ImageButton downloadFilesBtn;

    private RelativeLayout layoutDescriptionFile;
    private EditText commentContent;
    private TextView titlePostTv;
    private TextView descFiles;
    private TextView contentPostTv;

    public static final String POST_EXTRA_NAME = "POST";
    public static final String ID_GROUP_EXTRA_NAME = "ID_GROUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        downPostDetails = (ImageButton)findViewById(R.id.downPostDetails);
        upPostDetails = (ImageButton)findViewById(R.id.upPostDetails);
        downloadFilesBtn = (ImageButton)findViewById(R.id.downloadFilePostDetails);

        layoutDescriptionFile = (RelativeLayout)findViewById(R.id.layoutDescriptionFile);
        commentContent        = findViewById(R.id.contentCommentPostDetails);
        sendComment           = findViewById(R.id.sendCommentPostDetails);
        titlePostTv           = findViewById(R.id.titlePostDetails);
        descFiles             = findViewById(R.id.descFilesPostDetails);
        contentPostTv         = findViewById(R.id.contentPostDetails);


        //initialize ads
        MobileAds.initialize(this,SAMPLE_APMOB_ID);

        //video will be displayed
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        //to load the video
        loadVideoAd();

        //extra retrieved post and id of the group
        final Post post = (Post) getIntent().getSerializableExtra(POST_EXTRA_NAME);
        final String idGroup = getIntent().getStringExtra(ID_GROUP_EXTRA_NAME);
        curPost = post;

//        if post already marked by the user
        if(postAlreadyMarked(post,"negative")) {
            markDownBtn();
            demarkUpBtn();
        }
        else if( postAlreadyMarked(post,"positive")) {
            markUpBtn();
            demarkDownBtn();
        }

        //set description detail and title
        contentPostTv.setText(post.getTextContent());
        titlePostTv.setText(post.getTitle());

        //files of the post
        List<String> filesPaths = post.getFilesPaths();
        int size = filesPaths.size();
        if(size > 0) {
            String txt = filesPaths.get(0);
            if (size > 1) {
                txt += " , "+getString(R.string.and) +" "+ (size - 1) + " "+getString(R.string.others);
            }
            descFiles.setText(txt);
        }else{
            descFiles.setText(getString(R.string.no_files));
        }


        //listeners

        downPostDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkThread markThread = new MarkThread(v,post,idGroup);
                markThread.run();

            }
        });

        upPostDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkThread markThread = new MarkThread(v,post,idGroup);
                markThread.run();
            }
        });

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
                    sendComment.setImageResource(R.drawable.ic_send_grey_24dp);
                }
                else {
                    sendComment.setImageResource(R.drawable.ic_send_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //add permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }

        if(curPost.getFilesPaths().size() >= 1) {
            downloadFilesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApplication.getUser().getCoins() >= MIN_COINS_TO_DOWNLOAD_FILE) {
                        Toast.makeText(getApplicationContext(), getString(R.string.download_started), Toast.LENGTH_SHORT).show();
                        downloadFile();
                    }
                    else {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.post(new Runnable() {
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailsActivity.this);
                                builder.setMessage(getString(R.string.question_reload_coins));
                                builder.setNegativeButton(getString(R.string.no),null);
                                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(rewardedVideoAd.isLoaded()) {
                                            rewardedVideoAd.show();
                                        }
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                }
            });
        }else{
            downloadFilesBtn.setVisibility(View.INVISIBLE);
        }

        //comments of the post
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
        if (item.getItemId() == R.id.deletePostDetails) {
            //Toast.makeText(getApplicationContext(),"delete post",Toast.LENGTH_LONG).show();
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailsActivity.this);
                    builder.setMessage(R.string.confirm_delete_post);
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PostLink.deletePost(curPost);
                            FileLink.deleteFilesPost(curPost);
                            finish();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    private void loadVideoAd() {
        if(!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(SAMPLE_APMOB_UNIT_ID,new AdRequest.Builder().build());
        }
    }

    private void downloadFile(){
        List<String> filesPaths = curPost.getFilesPaths();
        OnSuccessFailureFileDownload sucessFailureListener = new OnSuccessFailureFileDownload();
        int i = 0;
        final File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "meapy/"+curPost.getDisciplineName()+"/"+curPost.getTitle());
        dir.mkdirs();
        for (String filepath : filesPaths) {
            final File localFile = new File(dir,filepath);
            String refStr = "data_groups/" + curPost.getGroupId() + "/"
                    + filepath;
            StorageReference ref = FirebaseStorage.getInstance().getReference(refStr);
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "file  download success "+dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    MyApplication.getUser().setCoins(MyApplication.getUser().getCoins() -  COINS_REMOVED_ON_DOWNLOADED_FILE); //set the coins of the user
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
            //si  créateur du post ou si admin
            if(menu != null && curPost.getUser_uid() != null && (fUser.getUid().equals(curPost.getUser_uid()) || MyApplication.getUser().getRank() == 1)){
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

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdLoaded() {}

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoAdClosed() {}

    @Override
    public void onRewarded(RewardItem rewardItem) {
        MyApplication.getUser().setCoins(MyApplication.getUser().getCoins()+ User.DEFAULT_NUMBER_COINS); //set user coins
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {}

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {}


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


    private class MarkThread implements Runnable {

        private Post post;
        private String idGroup;
        private ImageView imageView;

        public MarkThread(View view, Post post, String idGroup) {
            this.post = post;
            this.idGroup = idGroup;
            this.imageView = (ImageView)view;
        }

        @Override
        public synchronized void run() {
            String ref = "groups/" + idGroup + "/disciplines/" + post.getDisciplineId() + "/posts/" + post.getId();
            final DatabaseReference refPost = FirebaseDatabase.getInstance().getReference(ref);
            refPost.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post p = (Post)dataSnapshot.getValue(Post.class);
                    //positive
                    if(imageView.equals(upPostDetails)) {
                        if(postAlreadyMarked(p,"positive")) {
                            refPost.child("nbPositiveMark").setValue(p.getNbPositiveMark() - 1);
                            refPost.child("markedByUserId/" + MyApplication.getUser().getUid()).removeValue();
                            demarkUpBtn();
                        }
                        else if(postAlreadyMarked(p,"negative")) {
                            refPost.child("nbPositiveMark").setValue(p.getNbPositiveMark() + 1);
                            refPost.child("nbNegativeMark").setValue(p.getNbNegativeMark() - 1);
                            refPost.child("markedByUserId/" + MyApplication.getUser().getUid()).setValue("positive");
                            demarkDownBtn();
                            markUpBtn();
                        }
                        else {
                            refPost.child("nbPositiveMark").setValue(p.getNbPositiveMark() + 1);
                            markUpBtn();
                            refPost.child("markedByUserId/" + MyApplication.getUser().getUid()).setValue("positive");
                        }
                    }
                    //negative
                    else if(imageView.equals(downPostDetails)) {
                        if(postAlreadyMarked(p,"negative")) {
                            refPost.child("nbNegativeMark").setValue(p.getNbNegativeMark() - 1);
                            refPost.child("markedByUserId/" + MyApplication.getUser().getUid()).removeValue();
                            demarkDownBtn();
                        }
                        else if(postAlreadyMarked(p,"positive")) {
                            refPost.child("markedByUserId/" + MyApplication.getUser().getUid()).setValue("negative");
                            refPost.child("nbPositiveMark").setValue(p.getNbPositiveMark() - 1);
                            refPost.child("nbNegativeMark").setValue(p.getNbNegativeMark() + 1);
                            demarkUpBtn();
                            markDownBtn();
                        }
                        else {
                            refPost.child("nbNegativeMark").setValue(p.getNbNegativeMark() + 1);
                            markDownBtn();
                            refPost.child("markedByUserId/" + MyApplication.getUser().getUid()).setValue("negative");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    private void markDownBtn() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutDownPost);
        downPostDetails.setBackgroundTintList(ContextCompat.getColorStateList(PostDetailsActivity.this, R.color.lightGrey));
        layout.setBackground(ContextCompat.getDrawable(PostDetailsActivity.this, R.drawable.background_post_marked_down));
    }

    private void demarkDownBtn() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutDownPost);
        downPostDetails.setBackgroundTintList(ContextCompat.getColorStateList(PostDetailsActivity.this,android.R.color.holo_red_dark));
        layout.setBackground(ContextCompat.getDrawable(PostDetailsActivity.this,R.drawable.note_background_post));
    }

    private void markUpBtn() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutUpPost);
        upPostDetails.setBackgroundTintList(ContextCompat.getColorStateList(PostDetailsActivity.this,R.color.lightGrey));
        layout.setBackground(ContextCompat.getDrawable(PostDetailsActivity.this,R.drawable.background_post_marked_up));
    }

    private void demarkUpBtn() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutUpPost);
        upPostDetails.setBackgroundTintList(ContextCompat.getColorStateList(PostDetailsActivity.this,android.R.color.holo_green_dark));
        layout.setBackground(ContextCompat.getDrawable(PostDetailsActivity.this,R.drawable.note_background_post));
    }

    private boolean postAlreadyMarked(Post p, String posOrNeg) {
        if(p.getMarkedByUserId().containsKey(MyApplication.getUser().getUid())) {
            if(p.getMarkedByUserId().get(MyApplication.getUser().getUid()).equals(posOrNeg)) {
                return true;
            }
        }
        return false;
    }
}
