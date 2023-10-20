package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Objects;

public class change_user_details extends AppCompatActivity {

    TextInputEditText edt_Name;
    AutoCompleteTextView edt_User_Class;
    String name, usertype;
    Button btn_save;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference UserDB;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private CollectionReference ClothDB = DB.collection("Clothes");
    private CollectionReference FoodDB = DB.collection("Foods");
    private CollectionReference ShelterDB = DB.collection("Shelters");
    String[] UserClass = {"Donor", "Receiver"};
    ArrayAdapter<String> adapterClasses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_details);

        mAuth = FirebaseAuth.getInstance();
        UserDB = FirebaseDatabase.getInstance("https://share-it-6d179-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        user = mAuth.getCurrentUser();

        name = getIntent().getStringExtra("userName");
        usertype = getIntent().getStringExtra("userType");
        Log.d(name, usertype);

        edt_Name = findViewById(R.id.register_name);
        edt_User_Class = findViewById(R.id.register_user_type);

        adapterClasses = new ArrayAdapter<String>(this, R.layout.userclass, UserClass);
        progressBar = findViewById(R.id.change_progressbar);
        btn_save = findViewById(R.id.userd_change_btn);
        edt_User_Class.setAdapter(adapterClasses);

        String UserID = user.getUid();

        UserDB.child(UserID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                Log.d("UserID", UserID );
                Log.d("Username", String.valueOf(dataSnapshot.child("name").getValue()) );
                edt_Name.setText(name);
                if(usertype == "Donor") {
                    edt_User_Class.setListSelection(1);
                }else {
                    edt_User_Class.setListSelection(2);
                }

            }
        });


        edt_User_Class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usertype = String.valueOf(parent.getItemAtPosition(position));
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = String.valueOf(edt_Name.getText());
                UserDB.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
                UserDB.child(mAuth.getCurrentUser().getUid()).child("usertype").setValue(usertype).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(usertype == "Receiver"){
                            Query queryFD = FoodDB.whereEqualTo("DonorID", mAuth.getCurrentUser().getUid());
                            Query queryCD = ClothDB.whereEqualTo("DonorID", mAuth.getCurrentUser().getUid());
                            Query querySD = ShelterDB.whereEqualTo("DonorID", mAuth.getCurrentUser().getUid());

                            queryFD.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                        WriteBatch batch = DB.batch();

                                        for (DocumentSnapshot document : documents) {
                                            batch.delete(document.getReference());
                                        }

                                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(change_user_details.this, "Food contributions deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Error occurred while committing the batch write operation
                                                    Exception exception = task.getException();
                                                    Log.d("Food Contributions Deleting error: ", String.valueOf(exception));
                                                    Toast.makeText(change_user_details.this, "Error deleting Food contributions: "+ exception, Toast.LENGTH_SHORT).show();
                                                    // Handle the error case as needed
                                                }
                                            }
                                        });
                                    }else{
                                        // Error occurred while fetching the documents
                                        Exception exception = task.getException();
                                        Log.d("Food Contributions Fetching error: ", String.valueOf(exception));
                                        Toast.makeText(change_user_details.this, "Error fetching Food contributions: "+ exception, Toast.LENGTH_SHORT).show();
                                        // Handle the error case as needed
                                    }
                                }
                            });

                            queryCD.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                        WriteBatch batch = DB.batch();

                                        for (DocumentSnapshot document : documents) {
                                            batch.delete(document.getReference());
                                        }

                                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(change_user_details.this, "Cloth contributions deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Error occurred while committing the batch write operation
                                                    Exception exception = task.getException();
                                                    Log.d("Cloth Contributions Deleting error: ", String.valueOf(exception));
                                                    Toast.makeText(change_user_details.this, "Error deleting Cloth contributions: "+ exception, Toast.LENGTH_SHORT).show();
                                                    // Handle the error case as needed
                                                }
                                            }
                                        });
                                    }else{
                                        // Error occurred while fetching the documents
                                        Exception exception = task.getException();
                                        Log.d("Cloth Contributions Fetching error: ", String.valueOf(exception));
                                        Toast.makeText(change_user_details.this, "Error fetching Cloth contributions: "+ exception, Toast.LENGTH_SHORT).show();
                                        // Handle the error case as needed
                                    }
                                }
                            });

                            querySD.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                        WriteBatch batch = DB.batch();

                                        for (DocumentSnapshot document : documents) {
                                            batch.delete(document.getReference());
                                        }

                                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(change_user_details.this, "Shelter contributions deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Error occurred while committing the batch write operation
                                                    Exception exception = task.getException();
                                                    Log.d("Shelter Contributions Deleting error: ", String.valueOf(exception));
                                                    Toast.makeText(change_user_details.this, "Error deleting Shelter contributions: "+ exception, Toast.LENGTH_SHORT).show();
                                                    // Handle the error case as needed
                                                }
                                            }
                                        });
                                    }else{
                                        // Error occurred while fetching the documents
                                        Exception exception = task.getException();
                                        Log.d("Shelter Contributions Fetching error: ", String.valueOf(exception));
                                        Toast.makeText(change_user_details.this, "Error fetching Shelter contributions: "+ exception, Toast.LENGTH_SHORT).show();
                                        // Handle the error case as needed
                                    }
                                }
                            });

                        }
                        Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent_main);
                        finish();

                    }
                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}