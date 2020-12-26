package com.example.chatbox.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatbox.R;
import com.example.chatbox.adapters.HomeAdapter;
import com.example.chatbox.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class chat_fragment extends Fragment {
    RecyclerView recyclerView;
    DocumentReference documentReference;
    ArrayList<UserModel> usermodel;
    HomeAdapter hAdapter;
    FirebaseFirestore firestore;
    CollectionReference col;
    FirebaseAuth auth;


    public chat_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_chat_fragment, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        documentReference = firestore.collection("users").document(auth.getCurrentUser().getUid());


        usermodel = new ArrayList<>();

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // getting chat ids in user collection
        col = firestore.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection("Chat ids");
        col.orderBy("dateSet", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("onEvent",e+"");
                    return;
                }

                if (queryDocumentSnapshots != null) {
                        usermodel.clear();
                        for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {


                            usermodel.add(new UserModel(ds.getString("Name"), ds.getString("last Msg"), ds.getString("Time"), ds.getId(),ds.getString("ImageUri")));
                            hAdapter = new HomeAdapter(getContext(), usermodel);
                            recyclerView.setAdapter(hAdapter);
                            hAdapter.notifyDataSetChanged();
                        }
                    }
                 else
                    Log.d("onEvent","null");

            }
        });


        return  view;
    }

}