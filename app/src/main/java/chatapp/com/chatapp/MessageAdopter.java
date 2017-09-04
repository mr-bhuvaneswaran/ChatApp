package chatapp.com.chatapp;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by bhuvanes on 3/9/17.
 */

public class MessageAdopter extends RecyclerView.Adapter<MessageAdopter.MessageViewHolder> {

    private List<Message> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdopter(List<Message> mMessageList){
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.message_single_view, parent, false);
        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        private RelativeLayout message_text_layout;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            message_text_layout = itemView.findViewById(R.id.message_text_layout);
         }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message c = mMessageList.get(position);
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        String from = c.getFrom();
        if(!from.equals(current_user_id)){
            holder.message_text_layout.setGravity(Gravity.LEFT);
            holder.messageText.setBackgroundResource(R.drawable.custom_message_background);
        }
        else{
            holder.message_text_layout.setGravity(Gravity.RIGHT);
            holder.messageText.setBackgroundResource(R.drawable.custom_message_received);
        }

        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
