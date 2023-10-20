package com.example.shareit;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class sendFood extends AppCompatActivity {

    EditText foodName, foodQuantity;
    Button sendFood;
    FirebaseUser user;
    DatabaseReference UserDB;
    FirebaseAuth mAuth;
    FirebaseFirestore FoodDB;
    String UserID, UserName, UserPhone;
    Boolean UserVerification;
    Double LocLatitude, LocLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_food);

        foodName = findViewById(R.id.sendFood_FName);
        foodQuantity = findViewById(R.id.sendFood_Quantity);
        sendFood = findViewById(R.id.sendFood_Btn);
        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        FoodDB = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        UserID = user.getUid();
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


        UserDB.child(UserID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                UserName = String.valueOf(dataSnapshot.child("name").getValue());
                UserPhone = String.valueOf(dataSnapshot.child("phone").getValue());
                UserVerification = dataSnapshot.child("verification").getValue(Boolean.class);
            }
        });


        sendFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(foodName.getText())){
                    foodName.setError("Please enter food name");
                    return;
                } else if (TextUtils.isEmpty(foodQuantity.getText())) {
                    foodQuantity.setError("Please enter quantity");
                    return;
                }
                insertFoodData();
            }
        });

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LocLatitude = location.getLatitude();
                        LocLongitude = location.getLongitude();
                    }
                }
            });
        }
    }


    private void insertFoodData() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(LocLongitude == null){
                    LocLongitude = location.getLongitude();
                } else if (LocLatitude == null) {
                    LocLatitude = location.getLatitude();
                }

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Map<String, Object> FoodItem = new HashMap<>();

//                FoodItem foodItem = new FoodItem(user.getUid(), UserName, UserPhone, String.valueOf(foodName.getText()), String.valueOf(foodQuantity.getText()), true, UserVerification,   );

                getLocation();
                FoodItem.put("Hash", GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude), 5));
                FoodItem.put("DonorID", user.getUid());
                FoodItem.put("DonorName", UserName);
                FoodItem.put("DonorNumber", UserPhone);
                FoodItem.put("Verification", UserVerification);
                FoodItem.put("FoodName", String.valueOf(foodName.getText()));
                FoodItem.put("FoodCount", String.valueOf(foodQuantity.getText()));
                FoodItem.put("Location", new GeoPoint(LocLatitude, LocLongitude));
                FoodItem.put("TimeStamp", com.google.firebase.Timestamp.now().toDate());
                FoodItem.put("MilliSec", timestamp.getTime());
                FoodItem.put("Status", true);

                FoodDB.collection("Foods").add(FoodItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(sendFood.this, "Food Order: " + documentReference.getId() + "set successfully", Toast.LENGTH_SHORT).show();
                        Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent_main);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(sendFood.this, "Food Order unsuccessful \nError Code:  " + e , Toast.LENGTH_SHORT).show();
                        Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent_main);
                        finish();
                    }
                });
            }
        });

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(sendFood.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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

}