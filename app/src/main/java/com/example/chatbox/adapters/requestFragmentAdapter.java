package com.example.chatbox.adapters;

import android.annotation.SuppressLint;
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
import com.example.chatbox.models.UserModel;
import com.example.chatbox.models.friendModel;
import com.example.chatbox.models.reqModel;
import com.example.chatbox.userProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class requestFragmentAdapter extends RecyclerView.Adapter<requestFragmentAdapter.VH> {
    Context context;
    ArrayList<reqModel> userInfo= new ArrayList<>();
    StorageReference storageReference;

    public requestFragmentAdapter(Context context, ArrayList<reqModel> userInfo) {
        this.context = context;
        this.userInfo = userInfo;
    }

    @NonNull
    @Override
    public requestFragmentAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.recycler_home,parent,false);
        return new VH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final requestFragmentAdapter.VH holder, int position) {
        final reqModel mUser = userInfo.get(position);

        //getting current user dp from storage and displaying in view
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile = storageReference.child("users/"+mUser.getId());
        profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                    Picasso.get().load(task.getResult()).into(holder.profileImg);
                else
                    holder.profileImg.setImageResource(R.drawable.profile);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , userProfile.class);
                intent.putExtra("userid",mUser.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.txtMsg.setText("sent you a friend request");
        holder.txtTime.setVisibility(View.INVISIBLE);
        holder.txtName.setText(mUser.getName());

    }

    @Override
    public int getItemCount() {
        return userInfo.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView txtName,txtMsg,txtTime;
        ImageView profileImg;
        public VH(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMsg);
            txtName = itemView.findViewById(R.id.txtname);
            txtTime = itemView.findViewById(R.id.txtTime);
            profileImg = itemView.findViewById(R.id.profile_imageHome);
        }
    }
}
