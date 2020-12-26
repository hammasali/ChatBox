package com.example.chatbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chatbox.ui.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NavigationActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    DocumentReference documentReference;
    StorageReference sRef;

    ImageView imageView;
    TextView name, email;



    DrawerLayout drawer;
    NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sRef = FirebaseStorage.getInstance().getReference();
        documentReference = firestore.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation Build In Code


            //inflate header layout
            View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);

            //reference to views
            imageView = hView.findViewById(R.id.imageViewSlideBar);
            name = hView.findViewById(R.id.nameSlideBar);
            email = hView.findViewById(R.id.emailSlideBar);


            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.signout, R.id.profile, R.id.share, R.id.aboutus)
                    .setDrawerLayout(drawer)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);


        // Navigation Bar item menu selection code
      /*  navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {


                    case R.id.share:

                        ApplicationInfo api = getApplicationContext().getApplicationInfo();
                        String apkPatch = api.sourceDir;

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/vnd.android.package-archive");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(apkPatch));
                        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPatch)));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(Intent.createChooser(intent, "ShareVia"));
                        break;

                    case R.id.aboutus:
                        dialog.show();
                        break;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileFragment.class));
                        Toast.makeText(NavigationActivity.this, "profile", Toast.LENGTH_SHORT).show();

                    case  R.id.signout:
                        Toast.makeText(NavigationActivity.this, "signout", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });*/

        //getting current user dp from storage and displaying in view
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profile = storageReference.child("users/" + auth.getCurrentUser().getUid());
        profile.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                    Picasso.get().load(task.getResult()).into(imageView);
                else
                    imageView.setImageResource(R.drawable.profile);
            }
        });

        //getting current user info and displaying in view
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("Name"));
                email.setText(documentSnapshot.getString("Phone"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NavigationActivity.this, "Fetching info failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
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
                Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus(true);
    }

    public void updateUserStatus(boolean state) {
        String currentDate = DateFormat.getDateTimeInstance().format(new Date());


        HashMap<String, Object> map = new HashMap<>();
        map.put("offlineDate", currentDate);
        map.put("state", state);

        documentReference.update(map);
    }

}