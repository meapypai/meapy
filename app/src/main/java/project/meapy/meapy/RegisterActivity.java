package project.meapy.meapy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.BuilderColor;
import project.meapy.meapy.utils.Registration;

public class RegisterActivity extends MyAppCompatActivity {

//    private ErrorView errorView; //vu pour afficher l'erreur

    private Button signUpBtnIdRegister;

    private EditText firstNameIdRegister;
    private EditText lastNameIdRegister;
    private EditText emailIdRegister;
    private EditText passwordIdRegister;
    private TextView errorFieldRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signUpBtnIdRegister = (Button)findViewById(R.id.signUpBtnIdRegister);

        firstNameIdRegister = (EditText)findViewById(R.id.firstNameIdRegister);
        lastNameIdRegister  = (EditText)findViewById(R.id.lastNameIdRegister);
        emailIdRegister     = (EditText)findViewById(R.id.emailIdRegister);
        passwordIdRegister  = (EditText)findViewById(R.id.passwordIdRegister);
        errorFieldRegister  = (TextView)findViewById(R.id.errorFieldRegister);

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
                                user.setFirstName(firstName);
                                user.setLastName(lastName);
                                user.setChatBubbleColor(BuilderColor.generateHexaColor());

                                Registration registration = new Registration(RegisterActivity.this,email,password);
                                registration.register(user);
                            }
                            else {
                                errorMessage = getResources().getString(R.string.incorrect_password);
                            }
                        }
                        else {
                            errorMessage = getResources().getString(R.string.incorrect_mail);
                        }
                    }
                    else {
                        errorMessage = getResources().getString(R.string.incorrect_lastname);
                    }
                }
                else {
                    errorMessage = getResources().getString(R.string.incorrect_firstname);
                }

//                //creation de la vue d'erreur
//                if(errorView == null) {
//                    errorView = new ErrorView(RegisterActivity.this);
//
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                    RegisterActivity.this.addContentView(errorView, params);
//                }
                errorFieldRegister.setText(errorMessage);

                //on  peut de nouveau essayer de s'enregistrer
                signUpBtnIdRegister.setEnabled(true);
            }
        });
    }

    private boolean isValidName(String name) {
        Pattern pattern = Pattern.compile("[A-Za-z]{2,}");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidMail(String mail) {
        Pattern pattern = Pattern.compile("^\\w[\\w\\.]*\\w@\\w+\\.[a-z]{2,4}");
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }
}
