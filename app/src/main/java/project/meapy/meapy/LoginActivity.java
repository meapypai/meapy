package project.meapy.meapy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.logins.DefaultLogin;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends MyAppCompatActivity {

    private Button mSignButton;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mRegisterBtn;
    private TextView mForgotPassBtn;

    private EditText emailLogin;
    private EditText passwordLogin;

    private DefaultLogin defaultLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignButton = (Button)findViewById(R.id.email_sign_in_button);
        mRegisterBtn = (TextView)findViewById(R.id.register);
        mForgotPassBtn = (TextView)findViewById(R.id.password_forgot);


        emailLogin = (EditText)findViewById(R.id.emailLogin);
        passwordLogin = (EditText)findViewById(R.id.passwordLogin);

        mSignButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    defaultLogin = new DefaultLogin(LoginActivity.this,email,password);
                    defaultLogin.signIn();
                }
            }
        });

        mRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //mForgotPassBtn.setVisibility(View.INVISIBLE);
        mForgotPassBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText emailEdit = new EditText(LoginActivity.this);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        emailEdit.setLayoutParams(lp);
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setView(emailEdit);
                        builder.setMessage(R.string.write_your_email);
                        builder .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String email = emailEdit.getText().toString();
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.reset_pass_will_be_send), Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.reset_pass_failed), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        emailEdit.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                            @Override
                            public void afterTextChanged(Editable editable) {
                                String email = emailEdit.getText().toString();
                                if(email.length() > 0) {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                }else{
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){

            Intent intent = new Intent(LoginActivity.this, MyGroupsActivity.class);
            startActivity(intent);

        }
    }




}
