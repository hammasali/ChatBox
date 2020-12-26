package com.example.chatbox.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    StorageReference storageReference;
    StorageReference sRef = FirebaseStorage.getInstance().getReference();

    TextView pFullname, pEmail, pPhone, txtName, txtEmail;
    Button btnSave;
    ImageView img7, img9;
    EditText edtName, edtEmail;
    ImageView btnUploadImg, profileImage;

    boolean check1 = false, check2 = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_profile, container, false);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        pFullname = root.findViewById(R.id.profileFullName);
        pEmail = root.findViewById(R.id.profileEmail);
        pPhone = root.findViewById(R.id.profilePhone);
        btnSave = root.findViewById(R.id.button);
        img7 = root.findViewById(R.id.imageView7);
        img9 = root.findViewById(R.id.imageView9);
        txtName = root.findViewById(R.id.emailSlideBar);
        edtName = root.findViewById(R.id.edtperson);
        edtEmail = root.findViewById(R.id.edtEmail);
        txtEmail = root.findViewById(R.id.textView5);
        btnUploadImg = root.findViewById(R.id.btnUploadImg);
        profileImage = root.findViewById(R.id.profileImage);

        documentReference = firestore.collection("users").document(auth.getCurrentUser().getUid());
        final DocumentReference docRef = firestore.collection("users").document(auth.getCurrentUser().getUid());
        

        //getting current user dp from storage and displaying in view
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile = storageReference.child("users/" + auth.getCurrentUser().getUid());
        profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                    Picasso.get().load(task.getResult()).into(profileImage);
                 else
                    profileImage.setImageResource(R.drawable.profile);
            }
        });


        //upload image button
        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });

        //text Name button
        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtName.setVisibility(View.INVISIBLE);
                pFullname.setVisibility(View.INVISIBLE);
                edtName.setVisibility(View.VISIBLE);
                check1 = true;
            }
        });

        //text email button
        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEmail.setVisibility(View.INVISIBLE);
                pEmail.setVisibility(View.INVISIBLE);
                edtEmail.setVisibility(View.VISIBLE);
                check2 = true;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edtName.getText().toString();
                String userEmail = edtEmail.getText().toString();

                if ((check1 && !userName.isEmpty()) || (check2 && !userEmail.isEmpty())) {

                    if (check1) {
                        if (!userName.isEmpty()) {
                            pFullname.setText(userName);
                            txtName.setVisibility(View.VISIBLE);
                            pFullname.setVisibility(View.VISIBLE);
                            edtName.setVisibility(View.GONE);
                            check1 = false;
                        } else edtName.setError("Fill Credential");
                    } else
                        userName = pFullname.getText().toString();


                    if (check2) {
                        if (!userEmail.isEmpty()) {
                            pEmail.setText(userEmail);
                            txtEmail.setVisibility(View.VISIBLE);
                            pEmail.setVisibility(View.VISIBLE);
                            edtEmail.setVisibility(View.GONE);
                            check2 = false;
                        } else
                            edtEmail.setError("Fill Credential");
                    } else
                        userEmail = pEmail.getText().toString();


                    Map<String, Object> user = new HashMap<>();
                    user.put("Name", userName);
                    user.put("Email", userEmail);

                    docRef.update(user);
                }
                else Toast.makeText(getContext(), "Invalid", Toast.LENGTH_SHORT).show();
            }
        });

        //Displaying data from database to views
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    pFullname.setText(documentSnapshot.getString("Name"));
                    pEmail.setText(documentSnapshot.getString("Email"));
                    pPhone.setText(auth.getCurrentUser().getPhoneNumber());
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imageUri = data.getData();
                uploadImgtoDatabase(imageUri);
            }
        }
    }

    private void uploadImgtoDatabase(final Uri imageUri) {
        StorageReference fileRef = sRef.child("users/" + auth.getCurrentUser().getUid());

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //storing in firestore
                DocumentReference documentReference = firestore.collection("users").document(auth.getCurrentUser().getUid());
                Map<String, Object> i = new HashMap<>();
                i.put("ImageUrl", imageUri.toString());

                documentReference.update(i);

                //showing in view
                profileImage.setImageURI(imageUri);
                Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}