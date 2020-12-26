package com.example.chatbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatbox.R;
import com.example.chatbox.models.friendModel;
import com.example.chatbox.userProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchUserAdapter extends ArrayAdapter<friendModel> {
    private final ArrayList<friendModel> searchUsersFull;

    public SearchUserAdapter(@NonNull Context context, @NonNull List<friendModel> searchList) {
        super(context, 0, searchList);
        searchUsersFull = new ArrayList<>(searchList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.recycler_friendlist,parent,false);
        }
        TextView textViewName = convertView.findViewById(R.id.friendlistName);
        final ImageView imageViewFlag = convertView.findViewById(R.id.friendImg);

        final friendModel friendItem = getItem(position);


        if(friendItem != null)
        {
            textViewName.setText(friendItem.getName());


           StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference profile = storageReference.child("users/"+friendItem.getId());
            final View finalConvertView = convertView;
            profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                        Picasso.get().load(task.getResult()).into(imageViewFlag);
                    else
                        imageViewFlag.setImageResource(R.drawable.profile);


                    finalConvertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), userProfile.class);
                            intent.putExtra("userid",friendItem.getId());
                            getContext().startActivity(intent);
                        }
                    });
                }
            });
        }
        return convertView;
    }

    private final Filter searchFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results  =new FilterResults();
            ArrayList<friendModel> suggestions = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0)
                suggestions.addAll(searchUsersFull);
            else  {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(friendModel  item : searchUsersFull){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        suggestions.add(item);
                    }
                }
            }
                results.values = suggestions;
                results.count = suggestions.size();

                return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((friendModel) resultValue).getName();
        }
    };


}
