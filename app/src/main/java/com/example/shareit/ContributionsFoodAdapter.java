package com.example.shareit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ContributionsFoodAdapter extends FirestoreRecyclerAdapter<FoodItem, ContributionsFoodAdapter.FoodViewHolder> {

    Context context;

    public ContributionsFoodAdapter(@NonNull FirestoreRecyclerOptions<FoodItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contributionscard_item, parent, false);
        return new FoodViewHolder(v);
    }

    public void rightSwiped(int position){
        deleteItem(position);
    }

    public void leftSwiped(int position){
        if(getSnapshots().getSnapshot(position).getBoolean("Status")){
            changeStatus(position);
        }else {
            deleteItem(position);
        }
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void changeStatus(int position){
        getSnapshots().getSnapshot(position).getReference().update("Status", false);
    }

    public boolean getStatus(int position){
            return getSnapshots().getSnapshot(position).getBoolean("Status");
    }

    @Override
    protected void onBindViewHolder(@NonNull ContributionsFoodAdapter.FoodViewHolder holder, int position, @NonNull FoodItem model) {

        holder.Date.setText(model.TimeStamp.toDate().toLocaleString());
        holder.FoodName.setText(String.valueOf(model.FoodName));
        holder.FoodCount.setText(String.valueOf(model.FoodCount));

        if(!model.Status) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EEFC5E"));
        }
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView Date, FoodName, FoodCount;
        CardView cardView;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            FoodName = itemView.findViewById(R.id.contributionsItemName);
            FoodCount = itemView.findViewById(R.id.contributionsItemCount);
            Date = itemView.findViewById(R.id.contributionsItemDate);
            cardView = itemView.findViewById(R.id.contributionsItemCard);

        }
    }

}
