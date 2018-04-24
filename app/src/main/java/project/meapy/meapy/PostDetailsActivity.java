package project.meapy.meapy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import project.meapy.meapy.activities.MyAppCompatWithRewardedVideo;
import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.Notifier;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.comments.CommentAdapter;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.utils.BuilderFormatDate;
import project.meapy.meapy.utils.NotificationWorker;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.CommentLink;
import project.meapy.meapy.utils.firebase.FileLink;
import project.meapy.meapy.utils.firebase.PostLink;

public class PostDetailsActivity extends MyAppCompatWithRewardedVideo {

    public static final int MIN_COINS_TO_DOWNLOAD_FILE = 1;
    public static final int COINS_REMOVED_ON_DOWNLOADED_FILE = 1;

    private boolean isVisibleDetails = false;

    private Post curPost;
    private String idGroup;
    private Menu menu;

    private ImageButton downPostDetails;
    private ImageButton upPostDetails;
    private ImageButton sendComment;
    private ImageButton downloadFilesBtn;

    private RecyclerView recyclerViewComments;

    private RelativeLayout layoutDescriptionFile;
    private EditText commentContent;
    private TextView titlePostTv;
    private TextView descFiles;
    private TextView contentPostTv;

    //details post
    private TextView textViewSeeDetails;
    private ImageView iconSeeDetails;
    private LinearLayout detailsPost;
    private LinearLayout layoutSeeDetails;
    private TextView datePostDetails;

    private Animation animation; //animation of the icon details post

    private String userAdminId;

    public static final String POST_EXTRA_NAME = "POST";
    public static final String ID_GROUP_EXTRA_NAME = "ID_GROUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        downPostDetails = (ImageButton)findViewById(R.id.downPostDetails);
        upPostDetails = (ImageButton)findViewById(R.id.upPostDetails);
        downloadFilesBtn = (ImageButton)findViewById(R.id.downloadFilePostDetails);

        recyclerViewComments =  (RecyclerView)findViewById(R.id.commentsPostDetails);

        layoutDescriptionFile = (RelativeLayout)findViewById(R.id.layoutDescriptionFile);
        commentContent        = findViewById(R.id.contentCommentPostDetails);
        sendComment           = findViewById(R.id.sendCommentPostDetails);
        titlePostTv           = findViewById(R.id.titlePostDetails);
        descFiles             = findViewById(R.id.descFilesPostDetails);
        contentPostTv         = findViewById(R.id.contentPostDetails);

        //details posts
        textViewSeeDetails    = findViewById(R.id.textViewSeeDetails);
        iconSeeDetails        = findViewById(R.id.iconSeeDetails);
        detailsPost           = findViewById(R.id.detailsPost);
        layoutSeeDetails      = findViewById(R.id.layoutSeeDetails);
        datePostDetails       = findViewById(R.id.datePostDetails);

        animation = AnimationUtils.loadAnimation(PostDetailsActivity.this, R.anim.rotate_see_details_icon);
        //listener about see/hide details of post
        layoutSeeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisibleDetails) {
                    //start animation
                    animation.setFillAfter(true); //to keep the final position
                    iconSeeDetails.startAnimation(animation);

                    detailsPost.setVisibility(View.VISIBLE);
                    textViewSeeDetails.setText(getResources().getString(R.string.hide_details));
                    isVisibleDetails = true;
                }
                else {
                    //reset animation
                    animation.setFillAfter(false);
                    animation.reset();

                    detailsPost.setVisibility(View.GONE);
                    textViewSeeDetails.setText(getResources().getString(R.string.see_details));
                    isVisibleDetails = false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        sendComment.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
        layoutDescriptionFile.setBackgroundColor(colorSelectedOnSettings);
        layoutSeeDetails.setBackgroundColor(colorSelectedOnSettings);
    }

    private void configureUpDownMarkButton(){
        //       if post already marked by the user
        if(postAlreadyMarked(curPost,"negative")) {
            markDownBtn();
            demarkUpBtn();
        }
        else if( postAlreadyMarked(curPost,"positive")) {
            markUpBtn();
            demarkDownBtn();
        }
        //listeners
        downPostDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkThread markThread = new MarkThread(v,curPost,idGroup);
                markThread.run();

            }
        });

        upPostDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkThread markThread = new MarkThread(v,curPost,idGroup);
                markThread.run();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deletePostDetails:
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
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadFile(){
        //TODO : NOTIFICATION MUST BE FACTORIZE
        Uri selectedUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/meapy/" + curPost.getDisciplineName() + File.separator + curPost.getTitle());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent = Intent.createChooser(intent,getResources().getString(R.string.download_post));
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationWorker worker = new NotificationWorker(getApplicationContext());
        Notifier notifier = new Notifier();
        notifier.setTitle(getResources().getString(R.string.download_post));
        notifier.setContent(getResources().getString(R.string.download_post));
        int idNotif = curPost.getId();
        worker.make(notifier,intent,R.drawable.logo_app1_without_background,idNotif, Notification.FLAG_AUTO_CANCEL);

        /*NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default");
        mBuilder.setContentTitle(getResources().getString(R.string.download_post))
                .setContentText(getResources().getString(R.string.download_in_progress))
                .setSmallIcon(R.drawable.logo_app1_without_background)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent);

        mBuilder.setProgress(0, 0, true);*/
//        ------------------------------------------------------------------------------------
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

//                    Toast.makeText(getApplicationContext(), "file  download success "+dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    galleryAddPic(localFile);
                }
            })
                    .addOnFailureListener(sucessFailureListener);
        }
        MyApplication.getUser().setCoins(MyApplication.getUser().getCoins() -  COINS_REMOVED_ON_DOWNLOADED_FILE); //set the coins of the user


        //download  completed set the notification
        //mBuilder.setContentText(getResources().getString(R.string.download_finished)).setProgress(0,0,false);
        //notificationManager.notify(curPost.getId(), mBuilder.build());
        notifier.setContent(getResources().getString(R.string.download_finished));
        worker.make(notifier,intent,R.drawable.logo_app1_without_background,idNotif, Notification.FLAG_AUTO_CANCEL);
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            //si  créateur du post ou si admin
            if(menu != null && curPost.getUser_uid() != null &&
                    (fUser.getUid().equals(curPost.getUser_uid()) || MyApplication.getUser().getUid().equals(userAdminId))){
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

    private void fillDescriptionAndTitle(){
        contentPostTv.setText(curPost.getTextContent());
        titlePostTv.setText(curPost.getTitle());
    }

    private void configureFileRepresentation(){
        List<String> filesPaths = curPost.getFilesPaths();
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
    }

    private void configureSendCommentListener(){
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentTxt = commentContent.getText().toString();
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if(commentTxt.length() > 0 && fUser != null) {
                    Comment comment = new Comment();
                    comment.setDate(new Date());
                    comment.setContent(commentTxt);
                    comment.setPostId(curPost.getId());
                    comment.setUserId(fUser.getUid());
                    comment.setAuthorStr(fUser.getDisplayName());
                    if(MyApplication.getUser() != null) {
                        comment.setUser(MyApplication.getUser());
                    }
                    CommentLink.insertCommentToPost(comment,curPost);

                    commentContent.setText("");
                }
            }
        });
    }
    private void askPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }
    }
    private void configureDownloadButton(){
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
    }
    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        //extra retrieved post and id of the group
        final Post post = (Post) getIntent().getSerializableExtra(POST_EXTRA_NAME);
        idGroup = getIntent().getStringExtra(ID_GROUP_EXTRA_NAME);
        userAdminId = getIntent().getStringExtra(OneGroupActivity.EXTRA_GROUP_USER_CREATOR);
        curPost = post;

        //set date detail
        datePostDetails.setText(BuilderFormatDate.formatDateInLongFr(this,curPost.getDate()));

        fillDescriptionAndTitle();
        configureFileRepresentation();
        configureUpDownMarkButton();
        configureSendCommentListener();

        // ??
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

        askPermissions();
        configureDownloadButton();

        //comments of the post
        final List<Comment> comments = new ArrayList<>();
        final CommentAdapter adapter = new CommentAdapter(comments);

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(adapter);

        CommentLink.getCommentByPost(post,new RunnableWithParam(){
            @Override
            public void run() {
                comments.add((Comment)getParam());
                sortComment(comments);
                adapter.notifyDataSetChanged();
            }
        });
        super.onResume();
    }

    private void sortComment(List<Comment> comments){
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment t1) {
                Date d1 = comment.getDate();
                Date d2 = t1.getDate();
                if(d1.before(d2)){
                    return -1;
                }else if(d2.before(d1)){
                    return 1;
                }else {
                    return 0;
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }




    class OnSuccessFailureFileDownload implements OnSuccessListener,OnFailureListener{
        int i = 0;
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
                    //positive mark
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
                    //negative mark
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
