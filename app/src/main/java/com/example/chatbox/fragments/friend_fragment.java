package com.example.chatbox.fragments;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.R;
import com.example.chatbox.adapters.FriendListAdapter;
import com.example.chatbox.adapters.SearchUserAdapter;
import com.example.chatbox.models.friendModel;
import com.example.chatbox.models.friendModel2;
import com.example.chatbox.models.userSetters;
import com.example.chatbox.userProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class friend_fragment extends Fragment {
    ArrayList<String> lstAutoComplete = new ArrayList<>();
    ArrayList<userSetters> userData = new ArrayList<>();
    ArrayAdapter<String> adapter;
    AutoCompleteTextView searchUser;
    CollectionReference colRef,fcolRef;
    DocumentReference docRef;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    FirebaseAuth fauth;
    FriendListAdapter fAdapter;
    ArrayList<friendModel> fUser;
    ArrayList<friendModel2> fuser2;
    String temp;



    public friend_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_fragment, container, false);


        // Friend list recycler view
        recyclerView =view.findViewById(R.id.recyclerFriendList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fUser = new ArrayList<>();
        fuser2 = new ArrayList();

        //database refrence
        fauth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        temp = Objects.requireNonNull(fauth.getCurrentUser()).getUid();
        colRef=firestore.collection("users");

        //loading user from database to friend list
        fcolRef = firestore.collection("users").document(temp).collection("friends");
        loadUser();

        //loading in autocompletetext View
        searchUser =view.findViewById(R.id.searchUser);

        loadList();
        adapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,lstAutoComplete);
        searchUser.setAdapter(adapter);


        return  view;
    }


    //loading user friend list
    private void loadUser()  {

        fcolRef.whereEqualTo("RequestType","friend").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!= null){
                            Log.d("onEvent", e+"");
                            return;
                        }
                        if(queryDocumentSnapshots != null){
                            fuser2.clear();
                            for(QueryDocumentSnapshot ds : queryDocumentSnapshots){

                                docRef = firestore.collection("users").document(ds.getId());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        String name = Objects.requireNonNull(task.getResult()).getString("Name");
                                        String img = task.getResult().getString("ImageUrl");
                                        String Id = task.getResult().getString("UID");

                                        fuser2.add(new friendModel2(name, img, Id));

                                        fAdapter = new FriendListAdapter(getContext(), fuser2);
                                        recyclerView.setAdapter(fAdapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        else
                            Log.d("Error","Null");
                    }
                });

    }
    //serching and finding friend
    private void loadList() {


        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Log.d("Tag",e+"");
                 return;
                }


                if (queryDocumentSnapshots != null) {
                    fUser.clear();
                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {

                        if (!temp.equals(ds.getString("UID"))) {
                            String name = ds.getString("Name");
                            String img = ds.getString("ImageUrl");
                            final String Id = ds.getString("UID");

                            fUser.add(new friendModel(name, img, Id));
                        }
                        }
                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(),fUser);
                    searchUser.setAdapter(searchUserAdapter);
                    searchUserAdapter.notifyDataSetChanged();
                }
                else
                    Log.d("Error",e.getMessage()+"");

            }

        });
    }
}
/* colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
@Override
public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        fUser.clear();
        if(e == null){
        for (QueryDocumentSnapshot ds: queryDocumentSnapshots ){
        if (!fauth.getCurrentUser().getUid().equals(ds.getString("UID"))) {
        String name = ds.getString("Name");
        String img = ds.getString("ImageUrl");
        String Id = ds.getString("UID");

        fUser.add(new friendModel(name,img,Id));
        }
        }
        fAdapter = new FriendListAdapter(getContext(),fUser);
        recyclerView.setAdapter(fAdapter);
        adapter.notifyDataSetChanged();
        }else
        Log.d("Error",e.getMessage()+"");
        }
        });*/