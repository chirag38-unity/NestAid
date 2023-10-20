package com.example.shareit;

import android.content.Context;
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

public class ContributionsShelterAdapter extends FirestoreRecyclerAdapter<ShelterItem, ContributionsShelterAdapter.ShelterViewHolder> {

    Context context;

    public ContributionsShelterAdapter(@NonNull FirestoreRecyclerOptions<ShelterItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public ContributionsShelterAdapter.ShelterViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contributionscard_item, parent, false);
        return new ContributionsShelterAdapter.ShelterViewHolder(v);
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

//    @Override
//    public void onDataChanged () {
//        super.onDataChanged();
//        if (getItemCount() == 0)
//            Toast.makeText(context, "There are no Food items", Toast.LENGTH_SHORT).show();
//    }

    @Override
    protected void onBindViewHolder (@NonNull ContributionsShelterAdapter.ShelterViewHolder holder,
                                     int position, @NonNull ShelterItem model){

        holder.Date.setText(String.valueOf(model.TimeStamp.toDate().toLocaleString()));
        holder.ShelterDesc.setText(String.valueOf(model.ShelterDescription));
        holder.ShelterCount.setText(String.valueOf(model.ShelterAvailability));

        if(!model.Status)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EEFC5E"));

    }


    class ShelterViewHolder extends RecyclerView.ViewHolder {

        TextView Date, ShelterDesc, ShelterCount;
        CardView cardView;

        public ShelterViewHolder(@NonNull View itemView) {
            super(itemView);
            ShelterDesc = itemView.findViewById(R.id.contributionsItemName);
            ShelterCount = itemView.findViewById(R.id.contributionsItemCount);
            Date = itemView.findViewById(R.id.contributionsItemDate);
            cardView = itemView.findViewById(R.id.contributionsItemCard);

        }
    }

}
