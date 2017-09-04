package chatapp.com.chatapp;

import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager mtabsPager;
    private SectionPageAdopter msectionPageAdopter;
    private TabLayout mtabs;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FAB");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    moveToStart();
                }
            }
        };

        mtabsPager = (ViewPager) findViewById(R.id.main_tabsPager);
        msectionPageAdopter= new SectionPageAdopter(getSupportFragmentManager());
        mtabsPager.setAdapter(msectionPageAdopter);

        mtabs = (TabLayout) findViewById(R.id.main_tabs);
        mtabs.setupWithViewPager(mtabsPager);
    }

    public void moveToStart() {
        Intent startIntent = new Intent( MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser != null){
            mUserRef.child("online").setValue("true");
        }
        else{
            moveToStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            moveToStart();
        }

        if (item.getItemId() == R.id.main_settings_btn){
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.main_users_btn){
            Intent usersIntent = new Intent(MainActivity.this,UsersActivity.class);
            mUserRef.child("online").setValue(true);
            startActivity(usersIntent);
        }

        return true;
    }
}
