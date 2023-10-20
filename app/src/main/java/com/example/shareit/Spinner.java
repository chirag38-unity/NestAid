package com.example.shareit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class Spinner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);
        android.widget.Spinner spinner = findViewById(R.id.user_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.donation_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected user type
                String donationType = parent.getItemAtPosition(position).toString();

                switch (donationType) {
                    case "":
                        break;
                    case "Clothes":
                        Intent intent_clothes = new Intent(getApplicationContext(), donation_cloth.class);
                        startActivity(intent_clothes);
                        Toast.makeText(parent.getContext(),donationType,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case "Food":
                        Intent intent_food = new Intent(getApplicationContext(), sendFood.class);
                        startActivity(intent_food);
                        Toast.makeText(parent.getContext(),donationType,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case "Shelter":
                        Intent intent_shelter = new Intent(getApplicationContext(), donation_shelter.class);
                        startActivity(intent_shelter);
                        Toast.makeText(parent.getContext(),donationType,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });}}
