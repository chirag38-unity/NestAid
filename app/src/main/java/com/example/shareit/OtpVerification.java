package com.example.shareit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;

public class OtpVerification extends AppCompatActivity {

    EditText input_number1, input_number2, input_number3, input_number4, input_number5, input_number6;
    TextView number_view, resend_label, skip_label;
    ProgressBar progressbar_verify_otp;
    String phone_number, get_otp_backend;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference UserDB;
    String UserID, UserName, UserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        final Button verify_button = findViewById(R.id.buttongetotp);
        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        phone_number = getIntent().getStringExtra("number");
        get_otp_backend = getIntent().getStringExtra("OTPBackend");
        progressbar_verify_otp = findViewById(R.id.progressbar_verify_otp);

        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            UserID = currentUser.getUid();
            UserDB.child(UserID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    UserName = String.valueOf(dataSnapshot.child("name").getValue());
                    UserPhone = String.valueOf(dataSnapshot.child("phone").getValue());
                }
            });
        }


        input_number1 = findViewById(R.id.inputotp1);
        input_number2 = findViewById(R.id.inputotp2);
        input_number3 = findViewById(R.id.inputotp3);
        input_number4 = findViewById(R.id.inputotp4);
        input_number5 = findViewById(R.id.inputotp5);
        input_number6 = findViewById(R.id.inputotp6);

        resend_label = findViewById(R.id.textresendotp);
        skip_label = findViewById(R.id.skip);

        number_view = findViewById(R.id.textmobileshownumber);
        number_view.setText(phone_number);




        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!input_number1.getText().toString().trim().isEmpty() && !input_number2.getText().toString().trim().isEmpty() && !input_number3.getText().toString().trim().isEmpty() && !input_number4.getText().toString().trim().isEmpty()) {
                    String enter_code_otp = input_number1.getText().toString() +
                            input_number2.getText().toString() +
                            input_number3.getText().toString() +
                            input_number4.getText().toString() +
                            input_number5.getText().toString() +
                            input_number6.getText().toString();

                    if (get_otp_backend != null) {
                        progressbar_verify_otp.setVisibility(View.VISIBLE);
                        verify_button.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                get_otp_backend, enter_code_otp
                        );

                        currentUser.updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                progressbar_verify_otp.setVisibility(View.GONE);
                                verify_button.setVisibility(View.VISIBLE);

                                UserDB.child(currentUser.getUid()).child("verification").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        Intent intent = new Intent(OtpVerification.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Toast.makeText(OtpVerification.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                progressbar_verify_otp.setVisibility(View.GONE);
                                verify_button.setVisibility(View.VISIBLE);
                                Toast.makeText(OtpVerification.this, "Enter the correct Otp", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(OtpVerification.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(OtpVerification.this, "please enter all numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });

        numberotpmove();

        resend_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
            }
        });

        skip_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                finish();
            }
        });

    }

    private void resendOTP() {

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

                currentUser.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        progressbar_verify_otp.setVisibility(View.GONE);
                        UserDB.child(currentUser.getUid()).child("verification").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                Toast.makeText(OtpVerification.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
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
                get_otp_backend = verificationId;
                PhoneAuthProvider.@NonNull ForceResendingToken mResendToken = token;
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(OtpVerification.this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
        progressbar_verify_otp.setVisibility(View.INVISIBLE);

    }


    private void numberotpmove() {

        input_number1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_number2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_number2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_number3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_number3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_number4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_number4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_number5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_number5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_number6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}