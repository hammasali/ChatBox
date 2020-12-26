package com.example.chatbox.ui;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatbox.R;
import com.example.chatbox.loginScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class SignOutFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    DocumentReference documentReference;
    ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        progressDialog  = new ProgressDialog(getContext());
        progressDialog.setTitle("Signing Out");
        progressDialog.setMessage("Please wait...!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        HashMap<String, Object> c = new HashMap<>();
        c.put("state",false);
        documentReference.update(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext(), loginScreen.class));
                    progressDialog.dismiss();
                }
                else
                    Toast.makeText(getContext(), "Error Signing out", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
/* public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);



        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }*/