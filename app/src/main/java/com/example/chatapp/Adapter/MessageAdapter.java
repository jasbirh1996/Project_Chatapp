package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int msg_type_left=0;
    public static final int msg_type_right=1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    FirebaseUser fuser;


    public MessageAdapter(Context mContext, List<Chat> mChat,String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==msg_type_right){
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);

        return new MessageAdapter.ViewHolder(view);
    }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);

            return new MessageAdapter.ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.showMessage.setText(chat.getMessage());
        if(imageurl.equals("default")){
            holder.profile_pic.setImageResource(R.mipmap.ic_launcher);
        }else{

            Picasso.get().load(imageurl).into(holder.profile_pic);
        }

        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.message_seen.setText("Seen");
            }else{
                holder.message_seen.setText("Delivered");
            }
        }else{
            holder.message_seen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
        public ImageView profile_pic;
        public  TextView message_seen;

        public ViewHolder(View itemView) {
            super(itemView);
            showMessage= itemView.findViewById(R.id.showMessage);
            profile_pic= itemView.findViewById(R.id.profile_pic);
            message_seen= itemView.findViewById(R.id.message_seen);
        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())) {
            return msg_type_right;
        }else{
            return  msg_type_left;
        }
    }
}
