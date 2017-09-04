package chatapp.com.chatapp;

import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String muserId;
    private Toolbar mToolbar;
    private TextView mDisplayName;
    private TextView mLastSeen;
    private CircleImageView mUserImage;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ImageView chat_add;
    private ImageView chat_send;
    private EditText chat_message;
    private RecyclerView mMessage_list;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdopter mAdopter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        muserId = getIntent().getStringExtra("user_id");
        final String muserName = getIntent().getStringExtra("user_name");
        String muserThumb = getIntent().getStringExtra("user_thumb");
        mToolbar = (Toolbar) findViewById(R.id.chat_app_bar);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(null);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View app_action_bar = inflater.inflate(R.layout.activity_chat_app_bar, null);
        actionBar.setCustomView(app_action_bar);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        mDisplayName = (TextView) findViewById(R.id.display_name);
        mUserImage = (CircleImageView) findViewById(R.id.chat_bar_image);
        mLastSeen = (TextView) findViewById(R.id.last_seen);
        mUserImage = (CircleImageView) findViewById(R.id.chat_bar_image);
        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mDisplayName.setText(muserName);
        Picasso.with(getApplicationContext()).load(muserThumb).placeholder(R.drawable.default_avatar).into(mUserImage);
        chat_add = (ImageView) findViewById(R.id.add);
        chat_message = (EditText) findViewById(R.id.message);
        chat_send = (ImageView) findViewById(R.id.send);

        mAdopter = new MessageAdopter(messageList);
        mMessage_list = (RecyclerView) findViewById(R.id.message_list);
        mLinearLayout = new LinearLayoutManager(this);
        mMessage_list.setHasFixedSize(true);
        mMessage_list.setLayoutManager(mLinearLayout);
        mMessage_list.setAdapter(mAdopter);


        loadMessages();

        mUserDatabase.child("Users").child(muserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Online = dataSnapshot.child("online").getValue().toString();
                if (Online.equals("true")){
                    mLastSeen.setText("Online");
                }
                else{
                    GettingTimeAgo gettingTimeAgo = new GettingTimeAgo();
                    long last_time = Long.parseLong(Online);
                    String last_seen_time = gettingTimeAgo.getTimeAgo(last_time, getApplicationContext());
                    mLastSeen.setText(last_seen_time);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserDatabase.child("Chats").child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(muserId)){
                    Map chatMap = new HashMap();
                    chatMap.put("seen",false);
                    chatMap.put("timeStamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/" + current_user_id + "/" + muserId, chatMap);
                    chatUserMap.put("Chats/" + muserId + "/" + current_user_id, chatMap);

                    mUserDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                System.out.println("Error occured");
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                chat_message.getText().clear();
            }
        });
    }

    private void loadMessages() {
        mUserDatabase.child("messages").child(current_user_id).child(muserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                mAdopter.notifyDataSetChanged();
                mMessage_list.post(new Runnable() {
                    @Override
                    public void run() {
                        mMessage_list.smoothScrollToPosition((mAdopter.getItemCount()));
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = chat_message.getText().toString();
        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + muserId + "/" + current_user_id;
            String chat_user_ref = "messages/" + current_user_id + "/" + muserId;

            DatabaseReference user_message_push = mUserDatabase.child("messages").child(current_user_id)
                    .child(muserId).push();

            String user_push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", current_user_id);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + user_push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + user_push_id, messageMap);

            mUserDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        System.out.println("Error occured");
                    }

                }
            });

        }
    }
}
