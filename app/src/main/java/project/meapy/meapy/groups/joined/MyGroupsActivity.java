package project.meapy.meapy.groups.joined;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.LoginActivity;
import project.meapy.meapy.MyAccountActivity;
import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.MyApplication;
import project.meapy.meapy.NotificationThread;
import project.meapy.meapy.R;
import project.meapy.meapy.SendFileActivity;
import project.meapy.meapy.SettingsActivity;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.groups.OneGroupActivity;
import project.meapy.meapy.utils.CodeGroupsGenerator;
import project.meapy.meapy.utils.RetrieveImage;
import project.meapy.meapy.utils.RunnableWithParam;
import project.meapy.meapy.utils.firebase.GroupLink;
import project.meapy.meapy.utils.firebase.NotificationLink;

/**
 * Created by yassi on 23/02/2018.
 */

public class MyGroupsActivity extends MyAppCompatActivity {

    private FloatingActionButton createGroupId;
    private ImageButton enterCodeMyGroups;
    private EditText codeIdMyGroups;

    final Map<Integer,GroupsForView> idGroups = new HashMap<>();
    final Map<GroupsForView, Groups> viewToBean = new HashMap<>();
    ListView listView;
    GroupsAdapter adapter;

    protected boolean toolbarToConfig(){
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        enterCodeMyGroups = findViewById(R.id.enterCodeMyGroups);
        codeIdMyGroups    = findViewById(R.id.codeIdMyGroups);
        listView          = findViewById(R.id.listMyGroups);

        adapter = new GroupsAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_2,new ArrayList<GroupsForView>());
        // listeners
        createGroupId = (FloatingActionButton)findViewById(R.id.createGroupId);
        createGroupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyGroupsActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        // providing datas
        provideGroups();
        condifureBtnToJoinGroup();
    }

    private void provideGroups(){
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GroupsForView dGrp = adapter.getItem(i);
                Groups grp = viewToBean.get(dGrp);
                Intent intent = new Intent(MyGroupsActivity.this, OneGroupActivity.class);
                intent.putExtra(OneGroupActivity.GROUP_NAME_EXTRA,grp);
                startActivity(intent);
            }
        });
        GroupLink.provideGroupsByCurrentuser(new RunnableWithParam() {
            @Override
            public void run() {
                onGroupAdded((Groups) getParam());
            }
        }, new RunnableWithParam() {
            @Override
            public void run() {
                onGroupRemoved(((Groups)getParam()).getId());
            }
        });
    }

    public void onGroupAdded(Groups added){
        if(!idGroups.containsKey(added.getId())) {
            // UPDATE UI
            GroupsForView dGrp = new GroupsForView(R.drawable.bdd, added.getName(), added.getSummary() + "",added.getImageName());
            dGrp.setId(added.getId());
            idGroups.put(added.getId(), dGrp);
            viewToBean.put(dGrp, added);
            adapter.add(dGrp);
        }
    }
    public void onGroupRemoved(int idGrp){
        GroupsForView dGrp = idGroups.remove(idGrp);
        viewToBean.remove(dGrp);
        adapter.remove(dGrp);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_groups, menu);

//        //to set drawable of account item
//        StorageReference ref = FirebaseStorage.getInstance().getReference("users_img_profil/" + MyApplication.getUser().getUid() + "/" + MyApplication.getUser().getNameImageProfil());
//        Glide.with(this).using(new FirebaseImageLoader()).load(ref).centerCrop().into(new SimpleTarget<GlideDrawable>() {
//                                                                             @Override
//                                                                             public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                                                                                 Bitmap bitmap1=((GlideBitmapDrawable) resource).getBitmap();
//                                                                                 RoundedBitmapDrawable circularBitmapDrawable =
//                                                                                         RoundedBitmapDrawableFactory.create(MyGroupsActivity.this.getApplicationContext().getResources(), bitmap1);
//                                                                                 circularBitmapDrawable.setCircular(true);
//                                                                                 menu.getItem(2).setIcon(circularBitmapDrawable);
//                                                                             }
//                                                                         });
//
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null ;
        switch(item.getItemId()) {
            case R.id.addFileId:
                intent = new Intent(this, SendFileActivity.class);
                break;
            case R.id.joinGrpMenu:
                sendDialogForJoinGroup();
                //intent = new Intent(getApplicationContext(), JoinGroupActivity.class);
                break;
            case R.id.myAccountId:
                intent = new Intent(this, MyAccountActivity.class);
        }
        if(intent != null) {
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendDialogForJoinGroup(){
        final EditText codeEdit = new EditText(getApplicationContext());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyGroupsActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                codeEdit.setLayoutParams(lp);
                builder.setView(codeEdit);
                builder.setMessage(R.string.title_join_group_menu);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String code = codeEdit.getText().toString();
                        GroupLink.joinGroupByCode(MyGroupsActivity.this,code);
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                codeEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String code = codeEdit.getText().toString();
                        if(code.length() != CodeGroupsGenerator.CODE_SIZE){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }else{
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        int idNotification = i.getIntExtra(NotificationThread.ID_NOTIFICATION,0);

        //suppression de la notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(idNotification);

        //supression de la notification de l'user dans la db
        NotificationLink.removeNotificationFromCurrentUser(idNotification);

        /// TEST
        // providing datas
        //provideGroups();

        /// FIN TEST
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        provideGroups();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(MyApplication.getUser() !=  null)
//            Toast.makeText(getApplicationContext(),"Bienvenue "+ MyApplication.getUser().getFirstName(),Toast.LENGTH_LONG).show();
        //to notify user if he's added to a group
        MyApplication.launch();

        //change widgets'color in terms of settings
        createGroupId.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }

    private void condifureBtnToJoinGroup() {
        enterCodeMyGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeIdMyGroups.getText().toString();
                if(!TextUtils.isEmpty(code)) {
                    GroupLink.joinGroupByCode(MyGroupsActivity.this,code);
                    codeIdMyGroups.setText("");
                }
            }
        });
       codeIdMyGroups.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           }

           @Override
           public void afterTextChanged(Editable editable) {
               String code = editable.toString();
                if(editable.toString().length() == CodeGroupsGenerator.CODE_SIZE){
                    GroupLink.joinGroupByCode(MyGroupsActivity.this,code);
                }
           }
       });
    }
}
