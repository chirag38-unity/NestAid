package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_user_password extends AppCompatActivity {

    TextInputEditText edt_old_pass, edt_new_pass, edt_renew_pass;
    String old_pass, new_pass, renew_pass;
    Button btn_save;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_password);

        edt_old_pass = findViewById(R.id.change_prev_password);
        edt_new_pass = findViewById(R.id.change_new_password);
        edt_renew_pass = findViewById(R.id.change_renew_password);
        btn_save = findViewById(R.id.password_change_btn);
        progressBar = findViewById(R.id.change_progressbar);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.INVISIBLE);
                old_pass = edt_old_pass.getText().toString();
                new_pass = edt_new_pass.getText().toString();
                renew_pass = edt_renew_pass.getText().toString();
                
                if(TextUtils.isEmpty(old_pass)){
                    edt_old_pass.setError("Please Enter Old Password");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                } else if (!old_pass.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
                    Toast.makeText(change_user_password.this, "Please enter old password correctly", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                } else if (TextUtils.isEmpty(new_pass)) {
                    edt_new_pass.setError("Please enter new password");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                } else if (!new_pass.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
                    Toast.makeText(change_user_password.this, "New password must have minimum eight characters, at least one letter, one number and one special character", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                } else if (TextUtils.isEmpty(renew_pass)) {
                    edt_renew_pass.setError("Field cant be empty");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                } else if (!TextUtils.equals(new_pass, renew_pass)) {
                    Toast.makeText(change_user_password.this, "New Password and Confirm Password should be same", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                }else {
                    reset_password(old_pass, new_pass);
                }

            }
        });

    }

    private void reset_password(String old_pass, String new_pass) {

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), old_pass);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.INVISIBLE);
                btn_save.setVisibility(View.VISIBLE);
                user.updatePassword(new_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(change_user_password.this, "Password reset successfully  ", Toast.LENGTH_SHORT).show();
                        Intent main = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(change_user_password.this, "There was some technical difficulty while reseting your password", Toast.LENGTH_SHORT).show();
                        mAuth.sendPasswordResetEmail(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(change_user_password.this, "Sending password reset link to your registered email", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                btn_save.setVisibility(View.VISIBLE);
                Toast.makeText(change_user_password.this, "Old password could not be verified, resending password reset link", Toast.LENGTH_SHORT).show();
                mAuth.sendPasswordResetEmail(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(change_user_password.this, "Old password could not be verified, sending password reset link", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(change_user_password.this, "Old password could not be verified, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}