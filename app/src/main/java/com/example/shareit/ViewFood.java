package com.example.shareit;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.geofire.core.GeoHash;
import com.firebase.geofire.core.GeoHashQuery;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViewFood extends AppCompatActivity {

    RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private CollectionReference FoodDB = DB.collection("Foods");
    ProgressDialog progressDialog;
    Double LocLatitude, LocLongitude;
    String UserID;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        Bundle loc = getIntent().getExtras();
        LocLongitude = loc.getDouble("LocLongitude");
        LocLatitude = loc.getDouble("LocLatitude");
        UserID = loc.getString("UserID");

        setUpRecyclerView();

    }


    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<FoodItem> foodOptions;

        if(LocLongitude != null && LocLatitude != null){
            Map<String, String> Neighbours;
            Neighbours = Geohash.neighbours(GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));
            List<String> neighbours = new ArrayList<>(Neighbours.values());
            neighbours.add(GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));

            Query query = FoodDB.whereIn("Hash", neighbours).whereEqualTo("Status", true).orderBy("TimeStamp", Query.Direction.DESCENDING);
             foodOptions = new FirestoreRecyclerOptions.Builder<FoodItem>()
                    .setQuery(query, FoodItem.class)
                    .build();
        }else{
            Query query = FoodDB.whereEqualTo("Status", true).orderBy("TimeStamp", Query.Direction.DESCENDING);
            foodOptions = new FirestoreRecyclerOptions.Builder<FoodItem>()
                    .setQuery(query, FoodItem.class)
                    .build();
        }

        foodAdapter = new FoodAdapter(foodOptions);
        recyclerView = findViewById(R.id.foodRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView.setAdapter(foodAdapter);

        foodAdapter.setOnMessageButtonClickListener(new FoodAdapter.onFoodItemMessageButtonClickListener() {
            @Override
            public void onMessageButtonClick(DocumentSnapshot documentSnapshot, int position) {
                FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                Uri uri = Uri.parse("smsto:" + foodItem.DonorNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Hey " + foodItem.DonorName +", is " + foodItem.FoodName + " still available?" );

                if (ActivityCompat.checkSelfPermission(ViewFood.this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    askSmsPermission();
                }
            }
        });

        foodAdapter.setOnCallButtonClickListener(new FoodAdapter.onFoodItemCallButtonClickListener() {
            @Override
            public void onCallButtonClick(DocumentSnapshot documentSnapshot, int position) {
                FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                Uri uri = Uri.parse("tel:" + foodItem.DonorNumber);
                Intent intent = new Intent(Intent.ACTION_CALL, uri);

                if (ActivityCompat.checkSelfPermission(ViewFood.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    askCallingPermission();
                }
            }
        });

        foodAdapter.setOnTrackButtonClickListener(new FoodAdapter.onFoodItemTrackButtonClickListener() {
            @Override
            public void onTrackButtonClick(DocumentSnapshot documentSnapshot, int position) {
                FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                Toast.makeText(ViewFood.this, "Donor Name : " + foodItem.DonorName + "\nDonor Number: " + foodItem.DonorNumber, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + foodItem.Location.getLatitude() + "," + foodItem.Location.getLongitude()));
                startActivity(intent);
            }
        });

        ///Ye uncomment nhi krna

//        foodAdapter.setOnFoodItemClickListener(new FoodAdapter.onFoodItemClickListener() {
//            @Override
//            public void onFoodItemClick(DocumentSnapshot documentSnapshot, int position) {
//                FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
//                Toast.makeText(ViewFood.this, "Donor Name : " + foodItem.DonorName + "\nDonor Number: " + foodItem.DonorNumber, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?daddr=" + foodItem.Location.getLatitude() + "," + foodItem.Location.getLongitude()));
//                startActivity(intent);
//                finish();
//            }
//        });

    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(ViewFood.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        foodAdapter.notifyDataSetChanged();
        foodAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        foodAdapter.notifyDataSetChanged();
        foodAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
        foodAdapter.notifyDataSetChanged();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {
            askCallingPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            askSmsPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        foodAdapter.startListening();
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        foodAdapter.startListening();
        foodAdapter.notifyDataSetChanged();
    }

    private void askSmsPermission() {
        ActivityCompat.requestPermissions(ViewFood.this, new String[] {android.Manifest.permission.SEND_SMS}, 100);
    }

    private void askCallingPermission() {
        ActivityCompat.requestPermissions(ViewFood.this, new String[] {android.Manifest.permission.CALL_PHONE}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();;
            }else{
                Toast.makeText(this, "Require Permission to call or send sms", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}