package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    if (auth.getCurrentUser() != null) {
                        //user is login
                        documentReference = firestore.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), Registration.class));
                                    finish();
                                }
                            }
                        });


                    } else {
                        startActivity(new Intent(MainActivity.this, loginScreen.class));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}

/*
code for deleting entire collection
        CollectionReference collectionReference = firestore.collection("chats");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                WriteBatch batch= firestore.batch();
           for (QueryDocumentSnapshot ds : queryDocumentSnapshots){
               batch.delete(ds.getReference());
           }
           batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   Toast.makeText(chatScreen.this, "deleted", Toast.LENGTH_SHORT).show();

               }
           });
            }
        });
*/


