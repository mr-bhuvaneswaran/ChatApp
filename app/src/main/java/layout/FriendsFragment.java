package layout;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import chatapp.com.chatapp.ChatActivity;
import chatapp.com.chatapp.Friends;
import chatapp.com.chatapp.ProfileActivity;
import chatapp.com.chatapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private DatabaseReference mfriendsdatabaseReference;
    private DatabaseReference muserdatabaseReference;

    private RecyclerView mrecyclerView;

    private View mView;

    private FirebaseAuth mAuth;

    private String mUserId;

    public FriendsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friends, container, false);
        mrecyclerView = (RecyclerView) mView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mfriendsdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(mUserId);
        mfriendsdatabaseReference.keepSynced(true);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        muserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        muserdatabaseReference.keepSynced(true);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mfriendsdatabaseReference

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {
                final String list_user_id = getRef(position).getKey();
                muserdatabaseReference.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userthumb = dataSnapshot.child("thumb").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        String useronline = dataSnapshot.child("online").getValue().toString();
                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setStatus(userStatus);
                        friendsViewHolder.setThumb(userthumb,getContext());
                        friendsViewHolder.setOnline(useronline);

                        friendsViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{"OPEN PROFILE", "SEND MESSAGE"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("SELECT ACTION");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i == 0){
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);
                                        }
                                        if (i==1){
                                            Intent userIntent = new Intent(getContext(), ChatActivity.class);
                                            userIntent.putExtra("user_id", list_user_id);
                                            userIntent.putExtra("user_name", userName);
                                            userIntent.putExtra("user_thumb", userthumb);
                                            startActivity(userIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }});
            }
        };
        mrecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mview;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setStatus(String status){
            TextView UserSingleView = mview.findViewById(R.id.user_single_status);
            UserSingleView.setText(status);
        }

        public void setName(String name){
            TextView userNameView = mview.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setThumb(String thumb, Context ctx){
            CircleImageView userImageView = mview.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb).placeholder(R.drawable.default_avatar).into(userImageView);
        }

        public void setOnline(String online){
            ImageView onlineImage = mview.findViewById(R.id.online_image);
            if (online.equals("true")) {
                onlineImage.setVisibility(View.VISIBLE);
            }
            else {
                onlineImage.setVisibility(View.INVISIBLE);
            }
        }


    }

}