package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class loginScreen extends AppCompatActivity {
    Button btnSend;
    TextView txtReg,state;
    EditText edtVerif,edtPhone;
    ProgressBar progressBar;
    FirebaseAuth auth;
    CountryCodePicker codePicker;
    CheckBox box;
    String verificationId;
    FirebaseFirestore firestore;
    FirebaseDatabase db;
    PhoneAuthProvider.ForceResendingToken token;
    boolean verification =false, reg=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        btnSend=findViewById(R.id.btnSend);
        txtReg=findViewById(R.id.txtReg);
        edtVerif=findViewById(R.id.edtVerif);
        edtPhone=findViewById(R.id.editPhone);
        progressBar=findViewById(R.id.progressBar);
        state=findViewById(R.id.state);
        auth=FirebaseAuth.getInstance();
        codePicker=findViewById(R.id.ccp);
        firestore=FirebaseFirestore.getInstance();
        box=findViewById(R.id.checkBox);
        db = FirebaseDatabase.getInstance();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (btnSend.getText().toString()) {

                    case "Login":
                        String userOTP= edtVerif.getText().toString();
                        if(!userOTP.isEmpty()&&userOTP.length()==6){
                            PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationId,userOTP);
                            verify(credential);
                        }
                        else
                            edtVerif.setError("Invalid Code");
                        break;
                    case "send":
                        if(box.isChecked()){
                        if(!verification)
                        {
                            if(!edtPhone.getText().toString().isEmpty() && edtPhone.getText().toString().length()==10){
                            String phoneNo= codePicker.getSelectedCountryCodeWithPlus()+edtPhone.getText().toString();
                            progressBar.setVisibility(View.VISIBLE);
                            state.setText("sending OTP...");
                            state.setVisibility(View.VISIBLE);
                            requestOTP(phoneNo);
                        }
                        else
                            edtPhone.setError("Phone no is not valid");}
                        break;
                }
                else
                box.setError("Agree to terms");}
            }
        });

        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtReg.setText("");
            }
        });

    }

    private void verify(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    DocumentReference docRef= firestore.collection("users").document(auth.getCurrentUser().getUid());
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                                finish();
                            }
                            else{ startActivity(new Intent(getApplicationContext(),Registration.class));
                                finish();}
                        }
                    });
                }
                else {
                    Toast.makeText(loginScreen.this, "Not successful", Toast.LENGTH_SHORT).show();
                }
                if (task.isSuccessful()){

                }
            }
        });
    }

    private void requestOTP(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
             verificationId=s;
             token=forceResendingToken;
             btnSend.setText("Login");
             progressBar.setVisibility(View.GONE);
             state.setVisibility(View.GONE);
             edtVerif.setVisibility(View.VISIBLE);
             verification=true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(loginScreen.this, "Expired. Resending Code...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            verify(phoneAuthCredential);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(loginScreen.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

}