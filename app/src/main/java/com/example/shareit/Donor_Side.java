package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Donor_Side extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
//    Button logout;
    CardView donateFood, donateClothes, donateShelter, viewContributions;
    TextView userDetail;
    FirebaseUser user;
    DatabaseReference UserDB;
    FirebaseAuth mAuth;

    DrawerLayout menu_drawer;
    ImageView menu_button;
    LinearLayout user_number_verify, change_user_details, change_user_phone_number, change_user_email, change_user_password, user_logout;
    TextView menu_user_name, menu_user_email, menu_user_number;
    ImageView menu_user_number_verified;
    String userName, userType, userNumber;

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_donor_side);

        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        menu_drawer = findViewById(R.id.drawer_layout);
        menu_button = findViewById(R.id.menu);

        menu_user_name = findViewById(R.id.drawer_user_name);
        menu_user_email = findViewById(R.id.drawer_user_email);
        menu_user_number = findViewById(R.id.drawer_user_number);
        menu_user_number_verified = findViewById(R.id.drawer_user_number_verified);

        user_number_verify = findViewById(R.id.drawer_user_number_verification);
        change_user_details = findViewById(R.id.drawer_change_userd);
        change_user_phone_number = findViewById(R.id.drawer_change_user_number);
        change_user_email = findViewById(R.id.drawer_change_user_email);
        change_user_password = findViewById(R.id.drawer_change_user_password);
        user_logout = findViewById(R.id.drawer_logout);

//        logout = findViewById(R.id.logout);

        donateFood = findViewById(R.id.donateFood);
        donateClothes = findViewById(R.id.donateClothes);
        donateShelter = findViewById(R.id.donateShelter);
        viewContributions = findViewById(R.id.viewContributions);

        userDetail = findViewById(R.id.user_details);
        user = mAuth.getCurrentUser();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }

        if(user == null){
            Intent intent_login = new Intent(getApplicationContext(), Login.class);
            startActivity(intent_login);
            finish();
        }else {
            String UserID = user.getUid();
            UserDB.child(UserID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    menu_user_name.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                    userName = String.valueOf(dataSnapshot.child("name").getValue());
                    userType = String.valueOf(dataSnapshot.child("usertype").getValue());
                    userNumber = String.valueOf(dataSnapshot.child("phone").getValue());
                    menu_user_email.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                    menu_user_number.setText(String.valueOf(dataSnapshot.child("phone").getValue()));
                    if(dataSnapshot.child("verification").getValue(Boolean.class)){
                        menu_user_number_verified.setVisibility(View.VISIBLE);
                    }else {
                        user_number_verify.setVisibility(View.VISIBLE);
                    }
                    userDetail.setText("Hello " + String.valueOf(dataSnapshot.child("name").getValue()) + ", what are you donating?" );

                }
            });
        }

        //User change listener

        //Drawer button listeners

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(menu_drawer);
            }
        });

        user_number_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

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

                        Toast.makeText(Donor_Side.this, "OTP sent to your number: " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                        Intent otp_ver = new Intent(getApplicationContext(),OtpVerification.class);
                        otp_ver.putExtra("OTPBackend", mVerificationId);
                        startActivity(otp_ver);
                        finish();

                    }
                };
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91" + userNumber)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(Donor_Side.this)                 // (optional) Activity for callback binding // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        change_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Donor_Side.this, change_user_details.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        change_user_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Donor_Side.this, change_user_phone.class);
            }
        });

        change_user_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Donor_Side.this, change_user_email.class);
            }
        });

        change_user_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Donor_Side.this, change_user_password.class);
            }
        });

        user_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                finish();
                Toast.makeText(Donor_Side.this, "Logged Out", Toast.LENGTH_SHORT).show();
            }
        });

        donateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent food_donate = new Intent(getApplicationContext(), sendFood.class);
                startActivity(food_donate);
                Toast.makeText(Donor_Side.this, "Donate Food", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        donateClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cloth_donate = new Intent(getApplicationContext(), donation_cloth.class);
                startActivity(cloth_donate);
                Toast.makeText(Donor_Side.this, "Donate Clothes", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        donateShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shelter_donate = new Intent(getApplicationContext(), donation_shelter.class);
                startActivity(shelter_donate);
                Toast.makeText(Donor_Side.this, "Donate Shelter", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        viewContributions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_contributions = new Intent(getApplicationContext(), View_Contributions.class);
                startActivity(intent_contributions);
            }
        });

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(Donor_Side.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();;
            }else{
                mAuth.signOut();
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                finish();
                Toast.makeText(this, "Permission is compulsory", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(menu_drawer);
    }

    @Override
    public void onBackPressed() {

        if(menu_drawer.isDrawerOpen(GravityCompat.START)){
            menu_drawer.closeDrawer(GravityCompat.START);
        }else{
            finish();
            super.onBackPressed();
        }
    }

}