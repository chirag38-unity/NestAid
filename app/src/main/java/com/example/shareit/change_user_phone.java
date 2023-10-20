package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class change_user_phone extends AppCompatActivity {

    EditText edt_Phone;
    Button btn_register;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference UserDB;
    String userID, phone;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_phone);

        edt_Phone = findViewById(R.id.input_mobile_number);
        btn_register = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.progressbar_sending_otp);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.INVISIBLE);
                phone = String.valueOf(edt_Phone.getText());

                if(TextUtils.isEmpty(phone)){
                    edt_Phone.setError("Please enter your phone number");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                } else if (phone.length() < 10) {
                    Toast.makeText(change_user_phone.this, "Please enter your phone number completely", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }

                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@io.reactivex.rxjava3.annotations.NonNull PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.

                        Log.d("onVerificationComplete", "Running code onVerificationComplete");

                        user.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                                String userId = userID;
                                Boolean verification = true;
                                UserDB.child(userID).child("phone").setValue(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        Toast.makeText(change_user_phone.this, "Verification Complete. User Data saved", Toast.LENGTH_SHORT).show();
                                        Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(main);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        UserDB.child(userID).child("phone").setValue(phone);
                                    }
                                });
                                progressBar.setVisibility(View.INVISIBLE);
                                btn_register.setVisibility(View.VISIBLE);

                            }
                        });
                    }

                    @Override
                    public void onVerificationFailed(@io.reactivex.rxjava3.annotations.NonNull FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.

                        Log.d("onVerificationFailed", "Running code onVerificationFailed");


                        // Show a message and update the UI
                    }

                    @Override
                    public void onCodeSent(@io.reactivex.rxjava3.annotations.NonNull String verificationId,
                                           @io.reactivex.rxjava3.annotations.NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.


                        // Save verification ID and resending token so we can use them later
                        Log.d("onCodeSent", "Running code onCodeSent");
                        @io.reactivex.rxjava3.annotations.NonNull String mVerificationId = verificationId;
                        PhoneAuthProvider.@io.reactivex.rxjava3.annotations.NonNull ForceResendingToken mResendToken = token;

                        UserDB.child(userID).child("phone").setValue(phone);
                        UserDB.child(userID).child("verification").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                Toast.makeText(change_user_phone.this, "OTP sent to your number: " + phone, Toast.LENGTH_SHORT).show();
                                Intent otp_ver = new Intent(getApplicationContext(),OtpVerification.class);
                                otp_ver.putExtra("OTPBackend", mVerificationId);
                                startActivity(otp_ver);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                UserDB.child(userID).child("phone").setValue(phone);
                                UserDB.child(userID).child("verification").setValue(false);
                                Intent otp_ver = new Intent(getApplicationContext(),OtpVerification.class);
                                otp_ver.putExtra("number", phone);
                                otp_ver.putExtra("OTPBackend", verificationId);
                                startActivity(otp_ver);
                                finish();
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_register.setVisibility(View.VISIBLE);
                    }
                };

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91" + phone.trim())       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(change_user_phone.this)                 // (optional) Activity for callback binding
                                // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
                progressBar.setVisibility(View.INVISIBLE);
                btn_register.setVisibility(View.VISIBLE);

            };

        });

    }
}