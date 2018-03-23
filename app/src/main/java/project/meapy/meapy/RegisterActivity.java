package project.meapy.meapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.meapy.meapy.bean.User;
import project.meapy.meapy.database.UserMapper;
import project.meapy.meapy.groups.joined.MyGroupsActivity;
import project.meapy.meapy.utils.Registration;

public class RegisterActivity extends MyAppCompatActivity {

    private ErrorView errorView; //vu pour afficher l'erreur

    private Button signUpBtnIdRegister;

    private EditText firstNameIdRegister;
    private EditText lastNameIdRegister;
    private EditText emailIdRegister;
    private EditText passwordIdRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signUpBtnIdRegister = (Button)findViewById(R.id.signUpBtnIdRegister);

        firstNameIdRegister = (EditText)findViewById(R.id.firstNameIdRegister);
        lastNameIdRegister  = (EditText)findViewById(R.id.lastNameIdRegister);
        emailIdRegister     = (EditText)findViewById(R.id.emailIdRegister);
        passwordIdRegister  = (EditText)findViewById(R.id.passwordIdRegister);

        signUpBtnIdRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpBtnIdRegister.setEnabled(false);

                String firstName = firstNameIdRegister.getText().toString();
                String lastName  = lastNameIdRegister.getText().toString();
                String email     = emailIdRegister.getText().toString();
                String password  = passwordIdRegister.getText().toString();

                String errorMessage = "";
                if(isValidName(firstName)) {
                    if(isValidName(lastName)) {
                        if(isValidMail(email)) {
                            if(isValidPassword(password)) {

                                //insertion d'un nouvel user
                                User user = new User(firstName,lastName,email);

                                Registration registration = new Registration(RegisterActivity.this,email,password);
                                registration.register(user);
                            }
                            else {
                                errorMessage = "Password not valid";
                            }
                        }
                        else {
                            errorMessage = "Mail not valid";
                        }
                    }
                    else {
                        errorMessage = "Lastname not valid";
                    }
                }
                else {
                    errorMessage = "Firstname not valid";
                }

                //creation de la vue d'erreur
                if(errorView == null) {
                    errorView = new ErrorView(RegisterActivity.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    RegisterActivity.this.addContentView(errorView, params);
                }
                errorView.setText(errorMessage);
            }
        });
    }

    private boolean isValidName(String name) {
        Pattern pattern = Pattern.compile("[A-Za-z]{4,}");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidMail(String mail) {
        Pattern pattern = Pattern.compile("^\\w[\\w\\.]*\\w@\\w+\\.[a-z]{2,4}"); //yassine.sansaoui@hotmail.fr
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }
}
