package project.meapy.meapy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.MyApplication;
import project.meapy.meapy.R;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.firebase.UserLogined;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by yassi on 11/03/2018.
 */

public class Registration {

    private Context context;
    private String mail;
    private String password;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public Registration(Context context, String mail, String password) {
        this.context   = context;
        this.mail      = mail;
        this.password  = password;
    }

    public void register(final User userBean) {
        auth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();
                            userBean.setUid(uid);
                            //insert user into nodes of database
                            FirebaseDatabase.getInstance().getReference("users/"+uid).setValue(userBean);

                            //set the user logined
                            MyApplication.setUser(userBean);

                            //set user's name
                            String displayName = userBean.getFirstName() + " " + userBean.getLastName(); //name will be display on chat, comment...
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName).build();

                            user.updateProfile(profileUpdates);

                            Toast.makeText(context,context.getResources().getString(R.string.welcome_new_user) + " " + userBean.getFirstName(),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MyGroupsActivity.class);
                            context.startActivity(intent);
                        }
                        else {
                            Log.w("error", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context,context.getResources().getString(R.string.registration_fail),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
