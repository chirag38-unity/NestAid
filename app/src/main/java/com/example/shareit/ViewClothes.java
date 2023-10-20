package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ViewClothes extends AppCompatActivity {

    RecyclerView recyclerView;
    private ClothAdapter clothAdapter;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private CollectionReference ClothDB = DB.collection("Clothes");
    ProgressDialog progressDialog;
    Double LocLatitude, LocLongitude;
    String UserID;
    FirebaseAuth mAuth;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_clothes);

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
        FirestoreRecyclerOptions<ClothItem> clothOptions;
        if(LocLongitude != null && LocLatitude != null){
            Map<String, String> Neighbours;
            Neighbours = Geohash.neighbours(GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));

            List<String> neighbours = new ArrayList<>(Neighbours.values());
            neighbours.add(GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));

            Query query = ClothDB.whereIn("Hash", neighbours).whereEqualTo("Status", true).orderBy("TimeStamp", Query.Direction.DESCENDING);
            clothOptions = new FirestoreRecyclerOptions.Builder<ClothItem>()
                    .setQuery(query, ClothItem.class)
                    .build();
        }else{
            Query query = ClothDB.whereEqualTo("Status", true).orderBy("TimeStamp", Query.Direction.DESCENDING);
            clothOptions = new FirestoreRecyclerOptions.Builder<ClothItem>()
                    .setQuery(query, ClothItem.class)
                    .build();
        }

        clothAdapter = new ClothAdapter(clothOptions);
        recyclerView = findViewById(R.id.clothRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView.setAdapter(clothAdapter);

        clothAdapter.setOnMessageButtonClickListener(new ClothAdapter.onClothItemMessageButtonClickListener() {
            @Override
            public void onMessageButtonClick(DocumentSnapshot documentSnapshot, int position) {
                ClothItem clothItem = documentSnapshot.toObject(ClothItem.class);
                Uri uri = Uri.parse("smsto:" + clothItem.DonorNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Hey " + clothItem.DonorName + ", is " + clothItem.ClothName + " still available?" );

                if (ActivityCompat.checkSelfPermission(ViewClothes.this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    askSmsPermission();
                }
            }
        });

        clothAdapter.setOnCallButtonClickListener(new ClothAdapter.onClothItemCallButtonClickListener() {
            @Override
            public void onCallButtonClick(DocumentSnapshot documentSnapshot, int position) {
                ClothItem clothItem = documentSnapshot.toObject(ClothItem.class);
                Uri uri = Uri.parse("tel:" + clothItem.DonorNumber);
                Intent intent = new Intent(Intent.ACTION_CALL, uri);

                if (ActivityCompat.checkSelfPermission(ViewClothes.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    askCallingPermission();
                }
            }
        });

        clothAdapter.setOnTrackButtonClickListener(new ClothAdapter.onClothItemTrackButtonClickListener() {
            @Override
            public void onTrackButtonClick(DocumentSnapshot documentSnapshot, int position) {
                ClothItem clothItem = documentSnapshot.toObject(ClothItem.class);
                Toast.makeText(ViewClothes.this, "Donor Name : " + clothItem.DonorName + "\nDonor Number: " + clothItem.DonorNumber, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + clothItem.Location.getLatitude() + "," + clothItem.Location.getLongitude()));
                startActivity(intent);
            }
        });

//        clothAdapter.setOnClothItemClickListener(new ClothAdapter.onClothItemClickListener() {
//            @Override
//            public void onClothItemClick(DocumentSnapshot documentSnapshot, int position) {
//                ClothItem clothItem = documentSnapshot.toObject(ClothItem.class);
//                Toast.makeText(ViewClothes.this, "Donor Name : " + clothItem.DonorName + "\nDonor Number: " + clothItem.DonorNumber, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?daddr=" + clothItem.Location.getLatitude() + "," + clothItem.Location.getLongitude()));
//                startActivity(intent);
//                finish();
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        clothAdapter.startListening();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {
            askCallingPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            askSmsPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clothAdapter.startListening();
        clothAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clothAdapter.startListening();
        clothAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clothAdapter.notifyDataSetChanged();
        clothAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clothAdapter.notifyDataSetChanged();
        clothAdapter.stopListening();
    }

    private void askSmsPermission() {
        ActivityCompat.requestPermissions(ViewClothes.this, new String[] {android.Manifest.permission.SEND_SMS}, 100);
    }

    private void askCallingPermission() {
        ActivityCompat.requestPermissions(ViewClothes.this, new String[] {android.Manifest.permission.CALL_PHONE}, 100);
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