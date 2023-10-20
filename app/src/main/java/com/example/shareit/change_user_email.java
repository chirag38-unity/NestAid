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
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_user_email extends AppCompatActivity {

    TextInputEditText edt_email;
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
        setContentView(R.layout.activity_change_user_email);

        edt_email = findViewById(R.id.change_email);
        btn_save = findViewById(R.id.change_email_btn);
        progressBar = findViewById(R.id.change_progressbar);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.INVISIBLE);
                if (TextUtils.isEmpty(email)) {
                    edt_email.setError("PLease enter email");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    Toast.makeText(change_user_email.this, "Please enter your email properly", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    return;
                }

                user.verifyBeforeUpdateEmail(String.valueOf(edt_email.getText())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_save.setVisibility(View.VISIBLE);
                        Toast.makeText(change_user_email.this, "Please verify using the link sent to new email to update", Toast.LENGTH_SHORT).show();
                        Intent main = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_save.setVisibility(View.VISIBLE);
                        Toast.makeText(change_user_email.this, "Please check if the email is correct", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}