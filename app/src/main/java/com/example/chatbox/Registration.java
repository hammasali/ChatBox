package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    Button btnSave;
    EditText editPerson, editEmail;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        btnSave=findViewById(R.id.btnSave);
        editPerson=findViewById(R.id.edtPersonName);
        editEmail=findViewById(R.id.edtEmail);
        auth= FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        UID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DocumentReference docRef = firestore.collection("users").document(UID);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                //DatabaseReference db;
                //FirebaseDatabase.getInstance().getReference().child("chatapp/profile/+udd");
                //Toast.makeText(Registration.this, "Saving...", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(Registration.this, Home.class));
                if(!(editPerson.toString().isEmpty()&&editEmail.toString().isEmpty())){
                    String userName=editPerson.getText().toString();
                    String userEmail=editEmail.getText().toString();
                    String userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                    Map<String,Object> user =new HashMap<>();
                    user.put("Name",userName);
                    user.put("Email",userEmail);
                    user.put("Phone",userPhone);
                    user.put("UID", auth.getCurrentUser().getUid());
                    user.put("ImageUrl", "default");
                    user.put("offlineDate", "default");
                    user.put("state", false);

                    docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                                finish();
                            }
                            else
                                Toast.makeText(Registration.this, "Data not inserted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(Registration.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}