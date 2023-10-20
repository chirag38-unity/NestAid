package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;


public class Register extends AppCompatActivity {

    TextInputEditText edt_Mail, edt_Password, edt_Conf_Password;
    String email, name, usertype, phone, password, conf_password;
    Button btn_register;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    TextView to_login;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference UserDB;
    Integer verification_counter = 0;
    private Handler vHandler = new Handler();
//
//    String[] UserClass = {"Donor", "Receiver"};
    String getotpbackend;
//    ArrayAdapter<String> adapterClasses;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent_main);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        edt_Mail = findViewById(R.id.register_email);
        edt_Password = findViewById(R.id.register_password);
        edt_Conf_Password = findViewById(R.id.register_password_confirm);


        btn_register = findViewById(R.id.register_btn);
        to_login = findViewById(R.id.to_login);
        progressBar = findViewById(R.id.register_progressbar);

        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.INVISIBLE);
                email = String.valueOf(edt_Mail.getText());
                password = String.valueOf(edt_Password.getText());
                conf_password = String.valueOf(edt_Conf_Password.getText());



                if(TextUtils.isEmpty(email) ||!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")){
                    Toast.makeText(Register.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }
                else if (TextUtils.isEmpty(password) ) {
                    Toast.makeText(Register.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
                    Toast.makeText(Register.this, "Password must have minimum eight characters, at least one letter, one number and one special character", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                } else if (TextUtils.isEmpty(conf_password) || !TextUtils.equals(password, conf_password)) {
                    Toast.makeText(Register.this, "Please Recheck Entered Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                btn_register.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {

                                    user = mAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Register.this, "Verify Email Sent. Please Verify quickly to proceed.", Toast.LENGTH_SHORT).show();
                                            progressDialog = new ProgressDialog(Register.this);
                                            progressDialog.show();
                                            progressDialog.setContentView(R.layout.progress_dialog);
                                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                            evRunnable.run();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, "Something went wrong with your email. Please Signup again.", Toast.LENGTH_LONG).show();
                                            user.delete();
                                            progressBar.setVisibility(View.GONE);
                                            btn_register.setVisibility(View.VISIBLE);
                                            Intent intent_signin = new Intent(getApplicationContext(), Register.class);
                                            startActivity(intent_signin);
                                            finish();
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    btn_register.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });

    }

    private Runnable evRunnable = new Runnable() {
        @Override
        public void run() {
            user.reload();
            if(user.isEmailVerified()){
                String UserID = user.getUid();
                Toast.makeText(Register.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
                vHandler.removeCallbacks(evRunnable);
                Intent intent_user_details = new Intent(getApplicationContext(), user_details_register.class);
                intent_user_details.putExtra("email", email);
                startActivity(intent_user_details);
                finish();
                progressDialog.dismiss();
            } else if (verification_counter > 30) {
                Toast.makeText(Register.this, "Email verification failed. Please Signup again.", Toast.LENGTH_LONG).show();
                user.delete();
                Intent intent_signin = new Intent(getApplicationContext(), Register.class);
                startActivity(intent_signin);
                finish();
            } else{
                verification_counter++;
                vHandler.postDelayed(evRunnable, 2000);
            }
        }
    };

}