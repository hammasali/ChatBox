package com.example.chatbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.R;
import com.example.chatbox.models.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class messgaeAdapter extends RecyclerView.Adapter<messgaeAdapter.VH>{
    Context context;
    ArrayList<ChatModel> mChat;

    public messgaeAdapter(Context context, ArrayList<ChatModel> mChat) {
        this.context = context;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.recycler_sender,parent,false);
        return new messgaeAdapter.VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        ChatModel chat = mChat.get(position);

        if(chat.getSender().equals(auth.getUid())) {
            holder.txtSender.setText(chat.getMessage());
            holder.timeSender.setText(chat.getTime());
            holder.recieverLayout.setVisibility(View.GONE);
            holder.senderLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.txtReciever.setText(chat.getMessage());
            holder.timeReciever.setText(chat.getTime());
            holder.recieverLayout.setVisibility(View.VISIBLE);
            holder.senderLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class VH extends RecyclerView.ViewHolder {
        TextView txtSender,timeSender,txtReciever,timeReciever;
        ImageView imageView;
        LinearLayout senderLayout,recieverLayout;
        public VH(@NonNull View itemView) {
            super(itemView);
            txtSender = itemView.findViewById(R.id.txtSender);
            txtReciever = itemView.findViewById(R.id.txtreciever);
            timeReciever=itemView.findViewById(R.id.timereciever);
            timeSender=itemView.findViewById(R.id.timeSender);
            senderLayout= itemView.findViewById(R.id.senderLayout);
            recieverLayout=itemView.findViewById(R.id.recieverLayout);

        }
    }
}
