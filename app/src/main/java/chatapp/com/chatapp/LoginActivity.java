package chatapp.com.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar lToolbar;
    private FirebaseAuth mAuth;
    private TextInputLayout lemail;
    private TextInputLayout lpassword;
    private Button lLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        lToolbar = (Toolbar) findViewById(R.id.login_bar);
        setSupportActionBar(lToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lemail = (TextInputLayout) findViewById(R.id.login_email);
        lpassword = (TextInputLayout) findViewById(R.id.login_pwd);
        lLogin = (Button) findViewById(R.id.start_login_btn);

        lLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(lemail,lpassword);
            }
        });
    }

    private void login(TextInputLayout lemail, TextInputLayout lpassword) {

        String email = lemail.getEditText().getText().toString();
        String password = lpassword.getEditText().getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Invalid Input !",Toast.LENGTH_SHORT);
        }
        else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Please Try Again !",Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }
}
