package com.example.chatbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.R;
import com.example.chatbox.chatScreen;
import com.example.chatbox.models.friendModel;
import com.example.chatbox.models.friendModel2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.VH> {
    Context context;
    ArrayList<friendModel2> fusers = new ArrayList<>();
    StorageReference storageReference;

    public FriendListAdapter(Context context, ArrayList<friendModel2> fusers) {
        this.context = context;
        this.fusers = fusers;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.recycler_friendlist,parent,false);
        return new FriendListAdapter.VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        final friendModel2 user = fusers.get(position);
        holder.friendlistName.setText(user.getName());


        //getting current user dp from storage and displaying in view
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile = storageReference.child("users/"+user.getId());
        profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                    Picasso.get().load(task.getResult()).into(holder.imageFrnd);
                else
                    holder.imageFrnd.setImageResource(R.drawable.profile);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context ,chatScreen.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fusers.size();
    }


    public class VH extends RecyclerView.ViewHolder {
        TextView friendlistName;
        ImageView imageFrnd;
        public VH(@NonNull View itemView) {
            super(itemView);
            friendlistName = itemView.findViewById(R.id.friendlistName);
            imageFrnd = itemView.findViewById(R.id.friendImg);
        }
    }
}
