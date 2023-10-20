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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewShelters extends AppCompatActivity {

    RecyclerView recyclerView;
    private ShelterAdapter shelterAdapter;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private CollectionReference ShelterDB = DB.collection("Shelters");
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
        setContentView(R.layout.activity_view_shelters);

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

        FirestoreRecyclerOptions<ShelterItem> shelterOptions;

        if(LocLongitude != null && LocLatitude != null){
            Map<String, String> Neighbours;
            Neighbours = Geohash.neighbours(GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));
            Log.d("My location", GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));
            Log.d("N", Neighbours.get("n"));
            List<String> neighbours = new ArrayList<>(Neighbours.values());
            neighbours.add(GeoFireUtils.getGeoHashForLocation(new GeoLocation(LocLatitude, LocLongitude),5));

            Query query = ShelterDB.whereIn("Hash", neighbours).whereEqualTo("Status", true).orderBy("TimeStamp", Query.Direction.DESCENDING);
            shelterOptions = new FirestoreRecyclerOptions.Builder<ShelterItem>()
                    .setQuery(query, ShelterItem.class)
                    .build();
        }else{
            Query query = ShelterDB.whereEqualTo("Status", true).orderBy("TimeStamp", Query.Direction.DESCENDING);
            shelterOptions = new FirestoreRecyclerOptions.Builder<ShelterItem>()
                    .setQuery(query, ShelterItem.class)
                    .build();
        }

        shelterAdapter = new ShelterAdapter(shelterOptions);
        recyclerView = findViewById(R.id.shelterRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView.setAdapter(shelterAdapter);

        shelterAdapter.setOnMessageButtonClickListener(new ShelterAdapter.onShelterItemMessageButtonClickListener() {
            @Override
            public void onMessageButtonClick(DocumentSnapshot documentSnapshot, int position) {
                ShelterItem shelterItem = documentSnapshot.toObject(ShelterItem.class);
                Uri uri = Uri.parse("smsto:" + shelterItem.DonorNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Hey " + shelterItem.DonorName + ", is the shelter still available?" );

                if (ActivityCompat.checkSelfPermission(ViewShelters.this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    askSmsPermission();
                }
            }
        });

        shelterAdapter.setOnCallButtonClickListener(new ShelterAdapter.onShelterItemCallButtonClickListener() {
            @Override
            public void onCallButtonClick(DocumentSnapshot documentSnapshot, int position) {
                ShelterItem shelterItem = documentSnapshot.toObject(ShelterItem.class);
                Uri uri = Uri.parse("tel:" + shelterItem.DonorNumber);
                Intent intent = new Intent(Intent.ACTION_CALL, uri);

                if (ActivityCompat.checkSelfPermission(ViewShelters.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    askCallingPermission();
                }
            }
        });

        shelterAdapter.setOnTrackButtonClickListener(new ShelterAdapter.onShelterItemTrackButtonClickListener() {
            @Override
            public void onTrackButtonClick(DocumentSnapshot documentSnapshot, int position) {
                ShelterItem shelterItem = documentSnapshot.toObject(ShelterItem.class);
                Toast.makeText(ViewShelters.this, "Donor Name : " + shelterItem.DonorName + "\nDonor Number: " + shelterItem.DonorNumber, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + shelterItem.Location.getLatitude() + "," + shelterItem.Location.getLongitude()));
                startActivity(intent);
            }
        });

//        shelterAdapter.setOnShelterItemClickListener(new ShelterAdapter.onShelterItemClickListener() {
//            @Override
//            public void onShelterItemClick(DocumentSnapshot documentSnapshot, int position) {
//                ShelterItem shelterItem = documentSnapshot.toObject(ShelterItem.class);
//                Toast.makeText(ViewShelters.this, "Donor Name : " + shelterItem.DonorName + "\nDonor Number: " + shelterItem.DonorNumber, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?daddr=" + shelterItem.Location.getLatitude() + "," + shelterItem.Location.getLongitude()));
//                startActivity(intent);
//                finish();
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        shelterAdapter.startListening();
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
        shelterAdapter.startListening();
        shelterAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        shelterAdapter.startListening();
        shelterAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        shelterAdapter.notifyDataSetChanged();
        shelterAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shelterAdapter.notifyDataSetChanged();
        shelterAdapter.stopListening();
    }

    private void askSmsPermission() {
        ActivityCompat.requestPermissions(ViewShelters.this, new String[] {android.Manifest.permission.SEND_SMS}, 100);
    }

    private void askCallingPermission() {
        ActivityCompat.requestPermissions(ViewShelters.this, new String[] {android.Manifest.permission.CALL_PHONE}, 100);
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