package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private EditText emailLogin;
    private EditText passwordLogin;

    private DefaultLogin defaultLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignButton = (Button)findViewById(R.id.email_sign_in_button);
        mRegisterBtn = (TextView)findViewById(R.id.register);


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
