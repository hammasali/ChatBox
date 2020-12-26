package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.EventListenerProxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class userProfile extends AppCompatActivity {
    ImageView userImage;
    Button sendButton,declineButton;
    TextView userName,userEmail,userPhoneNo;
    FirebaseFirestore firestore;
    DocumentReference documentReference,cDocref,uDocref,dc;
    CollectionReference collRef;
    FirebaseUser fAuth;
    String currentState;
    ProgressDialog progressDialog;


    public String getUser(){
        Intent intent = getIntent();
        return intent.getStringExtra("userid");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userEmail = findViewById(R.id.emailView);
        userName =findViewById(R.id.nameView);
        userPhoneNo =findViewById(R.id.phoneNoView);
        sendButton = findViewById(R.id.sendReqbutton);
        declineButton = findViewById(R.id.declineReqbutton);
        userImage = findViewById(R.id.userimageView);
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance().getCurrentUser();
        cDocref = firestore.collection("users").document(fAuth.getUid()).collection("friends").document(getUser());
        uDocref=  firestore.collection("users").document(getUser()).collection("friends").document(fAuth.getUid());
        dc = firestore.collection("users").document(fAuth.getUid());
        currentState = "Not_friend";

        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Uploading user profile
        documentReference=firestore.collection("users").document(getUser());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName.setText(documentSnapshot.getString("Name"));
                userEmail.setText(documentSnapshot.getString("Email"));
                userPhoneNo.setText(documentSnapshot.getString("Phone"));

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference profile = storageReference.child("users/"+getUser());
                profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                            Picasso.get().load(task.getResult()).into(userImage);
                        else
                            userImage.setImageResource(R.drawable.profile);

                        progressDialog.dismiss();
                    }
                });
            }
        });

        declineButton.setVisibility(View.GONE);

        // Request feature || Accept request feature
        cDocref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String reqType = task.getResult().getString("RequestType");

                    switch (Objects.requireNonNull(reqType)) {
                        case "recieved":
                            currentState = "req_recieved";
                            sendButton.setText("accept request");
                            declineButton.setVisibility(View.VISIBLE);
                            break;
                        case "sent":
                            currentState = "req_sent";
                            sendButton.setText("cancel friend request");
                            declineButton.setVisibility(View.GONE);
                            break;
                        case "friend":
                            currentState = "friend";
                            sendButton.setText("unfriend");
                            declineButton.setVisibility(View.GONE);
                            break;
                    }
                    progressDialog.dismiss();
                }
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendButton.setEnabled(false);

                // Sending friend requests
                if(currentState.equals("Not_friend")){
                    Map<String, Object> cData = new HashMap<>();
                    cData.put("RequestType","sent");

                    cDocref.set(cData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> uData = new HashMap<>();
                                uData.put("RequestType","recieved");

                                uDocref.set(uData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        currentState = "req_sent";
                                        sendButton.setText("Cancel friend request");
                                        declineButton.setVisibility(View.GONE);
                                        Toast.makeText(userProfile.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else Toast.makeText(userProfile.this, "Request not sent", Toast.LENGTH_SHORT).show();

                            sendButton.setEnabled(true);
                        }
                    });

                }

                //declining friend request
                if(currentState.equals("req_sent")||currentState.equals("friend")){
                    cDocref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uDocref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendButton.setEnabled(true);
                                    currentState ="Not_friend" ;
                                    sendButton.setText("send request");
                                    declineButton.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }

                //Accepting a friend request
                if(currentState.equals("req_recieved")){
                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                    final Map<String, Object> cData = new HashMap<>();
                    cData.put("RequestType","friend");
                    cData.put("currentFriendDate",currentDate);

                    cDocref.set(cData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            uDocref.set(cData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendButton.setEnabled(true);
                                    currentState = "friend";
                                    sendButton.setText("Unfriend");
                                    declineButton.setVisibility(View.GONE);
                                }
                            });
                        }
                    });

                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cDocref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uDocref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendButton.setEnabled(true);
                                currentState ="Not_friend" ;
                                sendButton.setText("send request");
                                declineButton.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        updateUserStatus(true);
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus(true);
    }

    public void updateUserStatus(boolean state) {
        String currentDate= DateFormat.getDateTimeInstance().format(new Date());


        HashMap<String, Object> map = new HashMap<>();
        map.put("offlineDate",currentDate);
        map.put("state", state);

        documentReference.update(map);
    }


}