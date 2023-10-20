package com.example.shareit;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference UserDB;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();;
            }else{
                Toast.makeText(this, "Require Permission", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent_login = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_login);
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        user = mAuth.getCurrentUser();

        if(user == null){
            Intent intent_login = new Intent(getApplicationContext(), Login.class);
            startActivity(intent_login);
            finish();
        }else {
            String UserID = user.getUid();
            UserDB.child(UserID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (user.getEmail() != String.valueOf(dataSnapshot.child("email").getValue())){
                        UserDB.child(UserID).child("email").setValue(user.getEmail());
                    }
                    String usertype = String.valueOf(dataSnapshot.child("usertype").getValue());
                    if(usertype.equals("Donor")){
                        Intent intent_donor = new Intent(getApplicationContext(), Donor_Side.class);
                        startActivity(intent_donor);
                        finish();
                    } else if (usertype.equals("Receiver")) {
                        Intent intent_receiver = new Intent(getApplicationContext(), Receiver_Side.class);
                        startActivity(intent_receiver);
                        finish();
                    }
                    else {
                        mAuth.signOut();
                        Intent intent_login = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent_login);
                        finish();
                        Toast.makeText(MainActivity.this, "Error fetching user details\nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}