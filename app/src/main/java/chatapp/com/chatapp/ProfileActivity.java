package chatapp.com.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mDisplayName;
    private TextView mStatus;
    private TextView mTotalFriends;
    private Button mButton;
    private Toolbar mToolbar;
    private DatabaseReference mUserdatabaseReference;
    private DatabaseReference mFriendReqdatabaseReference;
    private DatabaseReference mFrienddatabaseReference;

    private String mcurrentState;
    private FirebaseUser mcurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mToolbar = (Toolbar) findViewById(R.id.profile_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ImageView mImageView = (ImageView) findViewById(R.id.profile_image_view);
        final TextView mDisplayName = (TextView) findViewById(R.id.profile_display_name);
        final TextView mStatus = (TextView) findViewById(R.id.profile_status);
        TextView mTotalFriends = (TextView) findViewById(R.id.profile_total_friends);
        final Button msendRequestButton = (Button) findViewById(R.id.profile_send_button);
        final Button mDeclineRequestButton = (Button) findViewById(R.id.profile_decline_button);
        mcurrentState = "not_friend";
        mUserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFrienddatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb = dataSnapshot.child("thumb").getValue().toString();

                mDisplayName.setText(displayName);
                mStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(thumb).placeholder(R.drawable.default_avatar).into(mImageView);

                mFriendReqdatabaseReference.child(mcurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){
                                mcurrentState = "req_received";
                                msendRequestButton.setText("Accept Friend Request");
                                mDeclineRequestButton.setVisibility(View.VISIBLE);
                                mDeclineRequestButton.setEnabled(true);
                            }
                            else if(req_type.equals("sent")){
                                mcurrentState = "req_sent";
                                msendRequestButton.setText("Cancel Friend Request");
                                mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                mDeclineRequestButton.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mcurrentState.equals("not_friend")){

                    msendRequestButton.setEnabled(false);

                    mFriendReqdatabaseReference.child(mcurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                 mFriendReqdatabaseReference.child(user_id).child(mcurrentUser.getUid()).child("request_type")
                                         .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         mcurrentState="req_sent";
                                         msendRequestButton.setText("Cancel Friend Request");
                                         mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                         mDeclineRequestButton.setEnabled(false);
                                         Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                                     }
                                 });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this,"Request Failed",Toast.LENGTH_SHORT).show();
                            }
                            msendRequestButton.setEnabled(true);
                        }

                    });

                }

                if (mcurrentState.equals("req_sent")){
                    mFriendReqdatabaseReference.child(mcurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqdatabaseReference.child(user_id).child(mcurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    msendRequestButton.setEnabled(true);
                                                    mcurrentState="not_friend";
                                                    msendRequestButton.setText("Send Friend Request");
                                                    mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                    mDeclineRequestButton.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }

                if (mcurrentState.equals("req_received")){

                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    mFrienddatabaseReference.child(mcurrentUser.getUid()).child(user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFrienddatabaseReference.child(user_id).child(mcurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqdatabaseReference.child(mcurrentUser.getUid()).child(user_id).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendReqdatabaseReference.child(user_id).child(mcurrentUser.getUid()).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    msendRequestButton.setEnabled(true);
                                                                    mcurrentState="friend";
                                                                    msendRequestButton.setText("Unfriend "+mDisplayName.getText());
                                                                    mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                                    mDeclineRequestButton.setEnabled(false);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                        }
                    });
                }

                if (mcurrentState.equals("friend")){
                    mFrienddatabaseReference.child(mcurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFrienddatabaseReference.child(user_id).child(mcurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    msendRequestButton.setEnabled(true);
                                                    mcurrentState="not_friend";
                                                    msendRequestButton.setText("Send Friend Request");
                                                }
                                            });
                                }
                            });
                }

            }
        });





    }
}
