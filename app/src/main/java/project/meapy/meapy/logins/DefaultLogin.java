package project.meapy.meapy.logins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.groups.joined.MyGroupsActivity;

/**
 * Created by sansaoui on 12/03/18.
 */

public class DefaultLogin {

    private Context c ;
    private String email;
    private String password;
    private FirebaseAuth mAuth;

    public DefaultLogin(Context c, String email , String password){
        this.c = c;
        this.email = email;
        this.password = password;
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(){

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("users/"+user.getUid()); //ajout dans la database

                    Intent intent = new Intent(c, MyGroupsActivity.class);
                    c.startActivity(intent);
                } else {
                    Toast.makeText(c, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isConnected(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }
}
