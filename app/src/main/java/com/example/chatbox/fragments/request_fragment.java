 package com.example.chatbox.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatbox.R;
import com.example.chatbox.adapters.requestFragmentAdapter;
import com.example.chatbox.models.friendModel;
import com.example.chatbox.models.reqModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class request_fragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<reqModel> fuser;
    FirebaseUser firebaseUser;
    CollectionReference colref;
    DocumentReference docref;
    FirebaseFirestore firestore;
    requestFragmentAdapter adapter;


    public request_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_fragment, container, false);
        recyclerView = view.findViewById(R.id.requestRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        fuser = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        colref = firestore.collection("users").document(firebaseUser.getUid()).collection("friends");

        load();


        return view;
    }

    private void load() {
        colref.whereEqualTo("RequestType", "recieved")
                .orderBy("currentFriendDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("onEvent", e + "");
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            fuser.clear();
                            for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                                final String userID = ds.getId();

                                docref = firestore.collection("users").document(userID);
                                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        String name = task.getResult().getString("Name");
                                        String img = task.getResult().getString("ImageUrl");

                                        //Toast.makeText(getContext(), "start", Toast.LENGTH_SHORT).show();
                                        fuser.add(new reqModel(name, img, userID));
                                    }
                                });
                            }
                            adapter = new requestFragmentAdapter(getContext(), fuser);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else
                            Log.d("Error", "Null");
                    }
                });

    }
}