package com.example.shareit;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class Receiver_Side extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    CardView receiveFood, receiveClothes, receiveShelter, logout;
    TextView userDetail;
    FirebaseUser user;
    DatabaseReference UserDB;
    FirebaseAuth mAuth;
    Double LocLatitude, LocLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    DrawerLayout menu_drawer;
    ImageView menu_button;
    LinearLayout user_number_verify, change_user_details, change_user_phone_number, change_user_email, change_user_password, user_logout;
    TextView menu_user_name, menu_user_email, menu_user_number;
    ImageView menu_user_number_verified;
    String userName, userType, userNumber;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_receiver_side);

        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        logout = findViewById(R.id.logout);
        receiveFood = findViewById(R.id.receiveFood);
        receiveClothes = findViewById(R.id.receiveClothes);
        receiveShelter = findViewById(R.id.receiveShelter);
        userDetail = findViewById(R.id.user_details);

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

        user = mAuth.getCurrentUser();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }

        if(user == null){
            Intent intent_login = new Intent(getApplicationContext(), Login.class);
            startActivity(intent_login);
            finish();
        }else {
            UserID = user.getUid();
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
                    userDetail.setText("Hello " + String.valueOf(dataSnapshot.child("name").getValue()) + ", what dou you need?");
                }
            });
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest1 = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 100).setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000).setMaxUpdateDelayMillis(100).build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(locationRequest1, locationCallback, null);
            return;
        }


        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        LocLatitude = location.getLatitude();
                        LocLongitude = location.getLongitude();
                    }
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);


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

                        Toast.makeText(Receiver_Side.this, "OTP sent to your number: " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
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
                                .setActivity(Receiver_Side.this)                 // (optional) Activity for callback binding // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        change_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Receiver_Side.this, change_user_details.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        change_user_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Receiver_Side.this, change_user_phone.class);
            }
        });

        change_user_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Receiver_Side.this, change_user_email.class);
            }
        });

        change_user_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Receiver_Side.this, change_user_password.class);
            }
        });

        user_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                finish();
                Toast.makeText(Receiver_Side.this, "Logged Out", Toast.LENGTH_SHORT).show();
            }
        });

        //Log out btn listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                Toast.makeText(Receiver_Side.this, "Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        //Grid button listeners
        receiveFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Receiver_Side.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                }
                LocationServices.getFusedLocationProviderClient(Receiver_Side.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(LocLongitude == null){
                            LocLongitude = location.getLongitude();
                        }
                        if (LocLatitude == null) {
                            LocLatitude = location.getLatitude();
                        }

                        Intent intent_getFood = new Intent(getApplicationContext(), ViewFood.class);
                        Bundle loc = new Bundle();
                        loc.putDouble("LocLongitude", LocLongitude);
                        loc.putDouble("LocLatitude", LocLatitude);
                        loc.putString("UserID", UserID);
                        intent_getFood.putExtras(loc);
                        Log.d("Longitude", String.valueOf(LocLongitude));
                        Log.d("Latitude", String.valueOf(LocLatitude));
                        startActivity(intent_getFood);
//                        finish();
                    }
                });


            }
        });

        receiveClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Receiver_Side.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                }
                LocationServices.getFusedLocationProviderClient(Receiver_Side.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(LocLongitude == null){
                            LocLongitude = location.getLongitude();
                        }
                        if (LocLatitude == null) {
                            LocLatitude = location.getLatitude();
                        }
                        Intent intent_getFood = new Intent(getApplicationContext(),ViewClothes.class);
                        Bundle loc = new Bundle();
                        loc.putDouble("LocLongitude", LocLongitude);
                        loc.putDouble("LocLatitude", LocLatitude);
                        loc.putString("UserID", UserID);
                        intent_getFood.putExtras(loc);
                        startActivity(intent_getFood);
//                      finish();

                    }
                });

            }
        });
        receiveShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(Receiver_Side.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                }
                LocationServices.getFusedLocationProviderClient(Receiver_Side.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(LocLongitude == null){
                            LocLongitude = location.getLongitude();
                        }
                        if (LocLatitude == null) {
                            LocLatitude = location.getLatitude();
                        }
                        Intent intent_getFood = new Intent(getApplicationContext(), ViewShelters.class);
                        Bundle loc = new Bundle();
                        loc.putDouble("LocLongitude", LocLongitude);
                        loc.putDouble("LocLatitude", LocLatitude);
                        loc.putString("UserID", UserID);
                        intent_getFood.putExtras(loc);
                        startActivity(intent_getFood);
                    }
                });
//                finish();
            }
        });
    }



    private void askPermission() {
        ActivityCompat.requestPermissions(Receiver_Side.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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
        activity.finish();
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

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(menu_drawer);
    }

}