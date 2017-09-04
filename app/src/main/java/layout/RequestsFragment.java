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
import chatapp.com.chatapp.Requests;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private DatabaseReference mfriendsdatabaseReference;
    private DatabaseReference muserdatabaseReference;

    private RecyclerView mrecyclerView;

    private View mView;

    private FirebaseAuth mAuth;

    private String mUserId;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_requests, container, false);
        mrecyclerView = mView.findViewById(R.id.friend_request_list);
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mfriendsdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(mUserId);
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

        FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestsViewHolder>(
                Requests.class,
                R.layout.users_single_layout,
                RequestsFragment.RequestsViewHolder.class,
                mfriendsdatabaseReference

        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder requestsViewHolder, Requests requests, int position) {
                final String list_user_id = getRef(position).getKey();
                muserdatabaseReference.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userthumb = dataSnapshot.child("thumb").getValue().toString();
                        requestsViewHolder.setName(userName);
                        requestsViewHolder.setStatus(userName);
                        requestsViewHolder.setThumb(userthumb,getContext());

                        requestsViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);
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

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        View mview;
        public RequestsViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setStatus(String name){
            TextView UserSingleView = mview.findViewById(R.id.user_single_status);
            UserSingleView.setText("New Request from " + name);
        }

        public void setName(String name){
            TextView userNameView = mview.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setThumb(String thumb, Context ctx){
            CircleImageView userImageView = mview.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb).placeholder(R.drawable.default_avatar).into(userImageView);
        }

    }
}
