package com.example.shareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class View_Contributions extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth mAuth;

    RecyclerView foodContributions, clothContributions, shelterContributions;
    FloatingActionButton addItem;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private CollectionReference FoodDB = DB.collection("Foods");
    private ContributionsFoodAdapter contributionsFoodAdapter;
    private ContributionsClothAdapter contributionsClothAdapter;
    private ContributionsShelterAdapter contributionsShelterAdapter;
    private CollectionReference ClothDB = DB.collection("Clothes");
    private CollectionReference ShelterDB = DB.collection("Shelters");

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contributions);

        addItem = findViewById(R.id.add_contributions);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setUpFoodRecyclerView();
        setUpClothRecyclerView();
        setUpShelterRecyclerView();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Spinner.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setUpShelterRecyclerView() {

        Query query = ShelterDB.whereEqualTo("DonorID", user.getUid()).orderBy("Status", Query.Direction.DESCENDING).orderBy("TimeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ShelterItem> shelterOptions = new FirestoreRecyclerOptions.Builder<ShelterItem>()
                .setQuery(query, ShelterItem.class)
                .build();

        contributionsShelterAdapter = new ContributionsShelterAdapter(shelterOptions);
        shelterContributions = findViewById(R.id.shelter_recycler);
        shelterContributions.setHasFixedSize(false);
        shelterContributions.setLayoutManager(new LinearLayoutManager(this));
        shelterContributions.setAdapter(contributionsShelterAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        contributionsShelterAdapter.leftSwiped(viewHolder.getAdapterPosition());
                        break;

                    case ItemTouchHelper.RIGHT:
                        contributionsShelterAdapter.rightSwiped(viewHolder.getAdapterPosition());
                        break;
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.yellow_card ))
                        .addSwipeLeftActionIcon(R.drawable.baseline_archive_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.red_card ))
                        .addSwipeRightActionIcon(R.drawable.baseline_delete_sweep_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(shelterContributions);

    }

    private void setUpClothRecyclerView() {

        Query query = ClothDB.whereEqualTo("DonorID", user.getUid()).orderBy("Status", Query.Direction.DESCENDING).orderBy("TimeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ClothItem> clothOptions = new FirestoreRecyclerOptions.Builder<ClothItem>()
                .setQuery(query, ClothItem.class)
                .build();

        contributionsClothAdapter = new ContributionsClothAdapter(clothOptions);
        clothContributions = findViewById(R.id.cloth_recycler);
        clothContributions.setHasFixedSize(false);
        clothContributions.setLayoutManager(new LinearLayoutManager(this));
        clothContributions.setAdapter(contributionsClothAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        contributionsClothAdapter.leftSwiped(viewHolder.getAdapterPosition());
                        break;

                    case ItemTouchHelper.RIGHT:
                        contributionsClothAdapter.rightSwiped(viewHolder.getAdapterPosition());
                        break;
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {



                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.yellow_card ))
                        .addSwipeLeftActionIcon(R.drawable.baseline_archive_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.red_card ))
                        .addSwipeRightActionIcon(R.drawable.baseline_delete_sweep_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(clothContributions);

    }

    private void setUpFoodRecyclerView() {

        Query query = FoodDB.whereEqualTo("DonorID", user.getUid()).orderBy("Status", Query.Direction.DESCENDING).orderBy("TimeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<FoodItem> foodOptions = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(query, FoodItem.class)
                .build();

        contributionsFoodAdapter = new ContributionsFoodAdapter(foodOptions);
        foodContributions = findViewById(R.id.food_recycler);
        foodContributions.setHasFixedSize(false);
        foodContributions.setLayoutManager(new LinearLayoutManager(this));
        foodContributions.setAdapter(contributionsFoodAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


                    switch (direction){
                        case ItemTouchHelper.LEFT:
                            contributionsFoodAdapter.leftSwiped(viewHolder.getAdapterPosition());
                            break;

                        case ItemTouchHelper.RIGHT:
                            contributionsFoodAdapter.rightSwiped(viewHolder.getAdapterPosition());
                            break;
                    }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

//                if(viewHolder.itemView.getDrawingCacheBackgroundColor() != Color.parseColor("#EEFC5E")){

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.yellow_card ))
                            .addSwipeLeftActionIcon(R.drawable.baseline_archive_24)
                            .addSwipeLeftLabel("DeActivate")
                            .setSwipeLeftLabelColor(ContextCompat.getColor(View_Contributions.this, R.color.white))
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.red_card ))
                            .addSwipeRightActionIcon(R.drawable.baseline_delete_sweep_24)
                            .addSwipeRightLabel("Delete")
                            .setSwipeRightLabelColor(ContextCompat.getColor(View_Contributions.this, R.color.white))
                            .setIconHorizontalMargin(TypedValue.COMPLEX_UNIT_SP, 20)
                            .create()
                            .decorate();

//                }else{

//                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.red_card ))
//                            .addSwipeLeftActionIcon(R.drawable.baseline_delete_sweep_24)
//                            .addSwipeRightBackgroundColor(ContextCompat.getColor(View_Contributions.this, R.color.red_card ))
//                            .addSwipeRightActionIcon(R.drawable.baseline_delete_sweep_24)
//                            .create()
//                            .decorate();

//                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(foodContributions);

    }

    @Override
    protected void onStart() {
        super.onStart();
        contributionsFoodAdapter.startListening();
        contributionsClothAdapter.startListening();
        contributionsShelterAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        contributionsFoodAdapter.stopListening();
        contributionsClothAdapter.stopListening();
        contributionsShelterAdapter.stopListening();
    }

}