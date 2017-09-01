package chatapp.com.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusUpdateActivity extends AppCompatActivity {

    private Toolbar sToolbar;

    private TextInputLayout sText;

    private Button sButton;

    private FirebaseUser user;

    private DatabaseReference statusDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);

        sToolbar = (Toolbar) findViewById(R.id.status_update_bar);
        setSupportActionBar(sToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        statusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        sText = (TextInputLayout) findViewById(R.id.status_text);
        sButton = (Button) findViewById(R.id.status_btn);

        String current_status = getIntent().getStringExtra("current_status");

        sText.getEditText().setText(current_status);
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_status = sText.getEditText().getText().toString();

                statusDatabase.child("status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StatusUpdateActivity.this,"Status Updated",Toast.LENGTH_SHORT).show();
                            Intent settings_intent = new Intent(StatusUpdateActivity.this,SettingsActivity.class);
                            startActivity(settings_intent);
                            finish();
                        }
                        else{
                            Toast.makeText(StatusUpdateActivity.this,"Status Update Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}

