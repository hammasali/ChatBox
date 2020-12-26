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
import com.example.chatbox.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {
    Context context;
    ArrayList<UserModel> userInfo= new ArrayList<>();
    StorageReference storageReference;

    public HomeAdapter(Context context, ArrayList<UserModel> userInfo) {
        this.context = context;
        this.userInfo = userInfo;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.recycler_home,parent,false);
        return new HomeAdapter.VH(view);
    }

    @Override
    public int getItemCount() {
        return userInfo.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
      final UserModel mUser = userInfo.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context ,chatScreen.class);
                intent.putExtra("userid",mUser.getUserID());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        //getting current user dp from storage and displaying in view
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile = storageReference.child("users/"+mUser.getUserID());
        profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                    Picasso.get().load(task.getResult()).into(holder.profileImg);
                else
                    holder.profileImg.setImageResource(R.drawable.profile);
            }
        });


        holder.txtName.setText(mUser.getUserName());
        holder.txtMsg.setText(mUser.getLastMsg());
        holder.txtTime.setText(mUser.getTime());

    }

    public class VH extends RecyclerView.ViewHolder{
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
