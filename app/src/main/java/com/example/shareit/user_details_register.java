package com.example.shareit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import io.reactivex.rxjava3.annotations.NonNull;

public class user_details_register extends AppCompatActivity {

    TextInputEditText edt_Name, edt_Phone;
    AutoCompleteTextView edt_User_Class;
    String name, usertype, phone, email, userID;
    Button btn_register;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference UserDB;

    String[] UserClass = {"Donor", "Receiver"};
    String getotpbackend;
    ArrayAdapter<String> adapterClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_register);

        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            email = user.getEmail();
        }

        edt_Name = findViewById(R.id.register_name);
        edt_Phone = findViewById(R.id.register_phone);
        edt_User_Class = findViewById(R.id.register_user_type);

        adapterClasses = new ArrayAdapter<String>(this, R.layout.userclass, UserClass);
        btn_register = findViewById(R.id.userd_save_btn);
        progressBar = findViewById(R.id.register_progressbar);

        edt_User_Class.setAdapter(adapterClasses);
        edt_User_Class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usertype = String.valueOf(parent.getItemAtPosition(position));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.INVISIBLE);
                name = String.valueOf(edt_Name.getText());
                phone = String.valueOf(edt_Phone.getText());

                if (TextUtils.isEmpty(name)) {
//                    Toast.makeText(user_details_register.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    edt_Name.setError("Please Enter Name");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                } else if (TextUtils.isEmpty(phone) || !phone.matches("\\d{10}")) {
//                    Toast.makeText(user_details_register.this, "Please Enter correct phone number", Toast.LENGTH_SHORT).show();
                    edt_Phone.setError("Please Enter correct phone number");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                } else if (TextUtils.isEmpty(usertype)) {
                    edt_User_Class.setError("Please Select User Type");
//                    Toast.makeText(user_details_register.this, "Please Select User Type", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }

                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
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
                                email = user.getEmail();
                                User user = new User(name, phone, email, usertype, userId, verification);
                                UserDB.child("Users").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        Toast.makeText(user_details_register.this, "Verification Complete. User Data saved", Toast.LENGTH_SHORT).show();
                                        Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(main);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        String userId = userID;
                                        Boolean verification = false;
                                        email = user.getEmail();
                                        User user = new User(name, phone, email, usertype, userId, verification);
                                        UserDB.child("Users").child(userID).setValue(user);
                                    }
                                });
                                progressBar.setVisibility(View.INVISIBLE);
                                btn_register.setVisibility(View.VISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                user.updatePhoneNumber(credential);
                                String userId = userID;
                                Boolean verification = true;
                                email = user.getEmail();
                                User user = new User(name, phone, email, usertype, userId, verification);
                                UserDB.child("Users").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        Toast.makeText(user_details_register.this, "Verification Complete. User Data saved", Toast.LENGTH_SHORT).show();
                                        Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(main);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        UserDB.child("Users").child(userID).setValue(user);
                                    }
                                });
                                progressBar.setVisibility(View.INVISIBLE);
                                btn_register.setVisibility(View.VISIBLE);

                            }
                        });
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.

                        Log.d("onVerificationFailed", "Running code onVerificationFailed");
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            //Invalid number
                            Toast.makeText(user_details_register.this, "Please check your number", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_register.setVisibility(View.VISIBLE);
                            return;
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            String userId = userID;
                            Boolean verification = false;
                            email = user.getEmail();
                            User user = new User(name, phone, email, usertype, userId, verification);
                            UserDB.child("Users").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    Toast.makeText(user_details_register.this, "Verification could not complete. Try again later. User Data saved", Toast.LENGTH_SHORT).show();
                                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(main);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@androidx.annotation.NonNull Exception e) {
                                    UserDB.child("Users").child(userID).setValue(user);
                                }
                            });
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_register.setVisibility(View.VISIBLE);
                            return;

                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_register.setVisibility(View.VISIBLE);
                            return;
                        }

                        // Show a message and update the UI
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.


                        // Save verification ID and resending token so we can use them later
                        Log.d("onCodeSent", "Running code onCodeSent");
                        @NonNull String mVerificationId = verificationId;
                        PhoneAuthProvider.@NonNull ForceResendingToken mResendToken = token;

                        String userId = userID;
                        Boolean verification = false;
                        email = user.getEmail();
                        User user = new User(name, phone, email, usertype, userId, verification);
                        UserDB.child("Users").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                Toast.makeText(user_details_register.this, "OTP sent to your number: " + phone, Toast.LENGTH_SHORT).show();
                                Intent otp_ver = new Intent(getApplicationContext(),OtpVerification.class);
                                otp_ver.putExtra("OTPBackend", mVerificationId);
                                startActivity(otp_ver);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                UserDB.child("Users").child(userID).setValue(user);
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
                                .setActivity(user_details_register.this)                 // (optional) Activity for callback binding
                                // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
                progressBar.setVisibility(View.INVISIBLE);
                btn_register.setVisibility(View.VISIBLE);

            }});

    }


}