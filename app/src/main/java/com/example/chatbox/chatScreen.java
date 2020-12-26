package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.adapters.messgaeAdapter;
import com.example.chatbox.models.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class chatScreen extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton sendBtn;
    EditText textMsg;
    ImageView profileImg;
    TextView userName,userState;
    SwipeRefreshLayout refreshMessages;
    messgaeAdapter adapter;

    ConstraintLayout constraintLayout;
    LinearLayout senderLayout;
    RelativeLayout receiverLayout;

    ArrayList<ChatModel> mChat;
    RecyclerView recyclerView;
    Calendar calendar;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference docref,documentReference;
    CollectionReference colref;
    StorageReference storageReference;
    DocumentSnapshot lastVisible;

    private static final int TOTAL_MESSAGES_TO_LOAD=15;
    String recieverImgUri, senderImgUri;
    boolean bool;
    int itemPos = 0,currentPage = 1;


    public String getRecieverImgUri() {
        return recieverImgUri;
    }

    public void setRecieverImgUri(String recieverImgUri) {
        this.recieverImgUri = recieverImgUri;
    }

    public String getSenderImgUri() {
        return senderImgUri;
    }

    public void setSenderImgUri(String senderImgUri) {
        this.senderImgUri = senderImgUri;
    }

    public String getUser(){
    Intent intent = getIntent();
    return intent.getStringExtra("userid");
}

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        toolbar =findViewById(R.id.toolbar5);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendBtn = findViewById(R.id.sendBtn);
        textMsg = findViewById(R.id.txtMessage);
        profileImg=findViewById(R.id.profile_imageChat);
        userName = findViewById(R.id.userName);
        userState = findViewById(R.id.userState);
        refreshMessages = findViewById(R.id.refreshMessages);
        mChat = new ArrayList<>();

       // senderLayout = findViewById(R.id.senderLayout);


        firestore =FirebaseFirestore.getInstance();
        auth =FirebaseAuth.getInstance();
        docref = firestore.collection("users").document(getUser());
        documentReference = firestore.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        //setting up online and last seen status
        docref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null)
                    Log.d("Event",e+"");
                if(documentSnapshot != null){
                    if(documentSnapshot.getBoolean("state").equals(true))
                        userState.setText("Online");
                    else
                        userState.setText(documentSnapshot.getString("offlineDate"));
                }
                else
                    Log.d("Error","Null");
            }
        });

        refreshMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;
                recieveMoreMessages(auth.getCurrentUser().getUid(),getUser());
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = textMsg.getText().toString().trim();
                String sender=auth.getCurrentUser().getUid();
                if (!message.isEmpty()){
                    //getting Current time
                    calendar=Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                    final String currentTime = simpleDateFormat.format(calendar.getTime());

                    //getting current everything
                    Calendar calendar2 = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    final String dateSet = simpleDateFormat1.format(calendar2.getTime());

                    sendMessage(sender,getUser(),message,currentTime,dateSet);
                }

                textMsg.setText("");
            }
        });

        constraintLayout=findViewById(R.id.toolbarLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), userProfile.class);
                intent.putExtra("userid",getUser());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });


        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName.setText(documentSnapshot.getString("Name"));

                recieveMessage(auth.getUid(),getUser());
                //Do coding For image
                storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference profile = storageReference.child("users/"+getUser());
                profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Picasso.get().load(task.getResult()).into(profileImg);
                    }
                });
            }
        });

        //messages recycler view
        recyclerView =findViewById(R.id.recyclerSenderView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


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

    }

    private void sendMessage(final String sender, final String reciever, final String message, final String currentTime, final String dateSet) {

        // getting reciever image from storage
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile = storageReference.child("users/"+reciever);
        profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                setRecieverImgUri(task.getResult().toString());
            }
        });

        //getting current user profile Image
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile1 = storageReference.child("users/"+sender);
        profile1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                setSenderImgUri(task.getResult().toString());
            }
        });

        //getting reciever name
        DocumentReference documentReference = firestore.collection("users").document(reciever);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                   String recieverName = documentSnapshot.getString("Name");

                //saving id of message reciever in currentUsername
                DocumentReference doc = firestore.collection("users").document(sender)
                        .collection("Chat ids").document(reciever);
                HashMap<String, String> s = new HashMap<>();
                s.put("Name",recieverName);
                s.put("dateSet",dateSet);
                s.put("last Msg",message);
                s.put("Time",currentTime);
                s.put("ImageUri",getRecieverImgUri());
                doc.set(s);



            }
        });

        //getting sender name
        DocumentReference documentReference2 = firestore.collection("users").document(sender);
        documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String senderName = documentSnapshot.getString("Name");
                // saving id of message sender in reciever
                DocumentReference doc2 = firestore.collection("users").document(reciever)
                        .collection("Chat ids").document(sender);
                HashMap<String, String> r = new HashMap<>();
                r.put("Name",senderName);
                r.put("dateSet",dateSet);
                r.put("last Msg",message);
                r.put("Time",currentTime);
                r.put("ImageUri",getSenderImgUri());
                doc2.set(r);

            }
        });


        //saving messages
      docref = firestore.collection("chats").document();
        HashMap<String , String> i = new HashMap<>();
        i.put("Message",message);
        i.put("Sender", sender);
        i.put("Reciever",reciever);
        i.put("Time",currentTime);
        i.put("dateSet",dateSet);
        docref.set(i);


    }
    private void  recieveMessage(final String sender , final String reciever) {

        colref = FirebaseFirestore.getInstance().collection("chats");

        Query next;
        //if(lastVisible == null)
            next = colref.orderBy("dateSet").limitToLast(currentPage*TOTAL_MESSAGES_TO_LOAD);
       // else
        //    next = colref.orderBy("dateSet").limitToLast(TOTAL_MESSAGES_TO_LOAD).endBefore(lastVisible);

        next.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!= null)
                    Log.d("onEvent", e+"");


                if(queryDocumentSnapshots != null){

                    mChat.clear();
                    //Load item first time
                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots){

                        if(ds.getString("Reciever").equals(sender) && ds.getString("Sender").equals(reciever) ||
                                ds.getString("Reciever").equals(reciever) && ds.getString("Sender").equals(sender)){

                           // if(bool){
                                mChat.add(new ChatModel(ds.getString("Sender"),ds.getString("Reciever"),ds.getString("Message"),ds.getString("Time")));
                           //     bool = false;
                           // }
                           //  else
                          //  mChat.add(itemPos++,new ChatModel(ds.getString("Sender"),ds.getString("Reciever"),ds.getString("Message"),ds.getString("Time")));
                        }
                    }


                    adapter =  new messgaeAdapter(getApplicationContext(),mChat);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                  //  if(lastVisible == null)
                    recyclerView.scrollToPosition(mChat.size()-1);
                 //   else
                 //   recyclerView.scrollToPosition(itemPos-1);

                    // Get the last visible document
                    if(queryDocumentSnapshots.size() > 0){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(0);
                        Log.d("VisibleDoc",lastVisible+"");
                    }

                    refreshMessages.setRefreshing(false);

                }
                else
                    Log.d("Document","null");
            }
        });

    }
    private  void recieveMoreMessages(final  String sender, final  String reciever){
        colref = FirebaseFirestore.getInstance().collection("chats");
        Query next;
        next = colref.orderBy("dateSet").limitToLast(TOTAL_MESSAGES_TO_LOAD).endBefore(lastVisible);

        next.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!= null)
                    Log.d("onEvent", e+"");


                if(queryDocumentSnapshots != null){

                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots){

                        if(ds.getString("Reciever").equals(sender) && ds.getString("Sender").equals(reciever) ||
                                ds.getString("Reciever").equals(reciever) && ds.getString("Sender").equals(sender)){
                            mChat.add(itemPos++,new ChatModel(ds.getString("Sender"),ds.getString("Reciever"),ds.getString("Message"),ds.getString("Time")));
                        }
                    }

                    adapter =  new messgaeAdapter(getApplicationContext(),mChat);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(itemPos-1);

                    if(queryDocumentSnapshots.size() > 0){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(0);
                        Log.d("VisibleDoc",lastVisible+"");
                    }

                    refreshMessages.setRefreshing(false);


                }
                else
                    Log.d("Document","null");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.delMenu:
                DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(auth.getCurrentUser().getUid())
                        .collection("Chat ids").document(getUser());
                doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                            Toast.makeText(chatScreen.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                            Toast.makeText(chatScreen.this, task.getException()+"", Toast.LENGTH_SHORT).show();
                    }
                });
              break;
        }
        return super.onOptionsItemSelected(item);
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
