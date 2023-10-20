package com.example.shareit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ShelterAdapter extends FirestoreRecyclerAdapter<ShelterItem, ShelterAdapter.ShelterViewHolder> {

//    private ShelterAdapter.onShelterItemClickListener listener;

    Context context;
    private onShelterItemMessageButtonClickListener messageButtonClickListener;
    private onShelterItemCallButtonClickListener callButtonClickListener;
    private onShelterItemTrackButtonClickListener trackButtonClickListener;

    public ShelterAdapter(@NonNull FirestoreRecyclerOptions<ShelterItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public ShelterAdapter.ShelterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sheltercard_item, parent, false);
        return new ShelterAdapter.ShelterViewHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if(getItemCount() == 0){

        }
//            Toast.makeText(context, "There are no Food items", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onBindViewHolder(@NonNull ShelterAdapter.ShelterViewHolder holder, int position, @NonNull ShelterItem model) {

        holder.DonorName.setText(String.valueOf(model.DonorName));
        holder.DonorNumber.setText(String.valueOf(model.DonorNumber));
        holder.ShelterDesc.setText(String.valueOf(model.ShelterDescription));
        holder.ShelterAvailability.setText(String.valueOf(model.ShelterAvailability));
        if(model.getVerification()){
            holder.donor_verification.setVisibility(View.VISIBLE);
        }

    }

    class ShelterViewHolder extends RecyclerView.ViewHolder {

        TextView DonorName, DonorNumber, ShelterDesc, ShelterAvailability;
        CardView cardView;
        Button send_message, call_donor, track_location;
        ImageView donor_verification;
        public ShelterViewHolder(@NonNull View itemView) {
            super(itemView);
            DonorName = itemView.findViewById(R.id.shelterDonorName);
            DonorNumber = itemView.findViewById(R.id.shelterDonorNumber);
            ShelterDesc = itemView.findViewById(R.id.shelterItemDesc);
            ShelterAvailability = itemView.findViewById(R.id.shelterItemCount);
            cardView = itemView.findViewById(R.id.shelterItemCard);
            donor_verification = itemView.findViewById(R.id.donor_verification);
            send_message = itemView.findViewById(R.id.message_donor);
            call_donor = itemView.findViewById(R.id.call_donor);
            track_location = itemView.findViewById(R.id.track_map);

            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && messageButtonClickListener != null) {
                        messageButtonClickListener.onMessageButtonClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            call_donor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && callButtonClickListener != null) {
                        callButtonClickListener.onCallButtonClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            track_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && trackButtonClickListener != null) {
                        trackButtonClickListener.onTrackButtonClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });



//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION && listener != null) {
//                        listener.onShelterItemClick(getSnapshots().getSnapshot(position), position);
//                    }
//                }
//            });

        }
    }

    public  interface onShelterItemMessageButtonClickListener {
        void onMessageButtonClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnMessageButtonClickListener(onShelterItemMessageButtonClickListener listener){
        this.messageButtonClickListener = listener;
    }

    public  interface onShelterItemCallButtonClickListener {
        void onCallButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnCallButtonClickListener(onShelterItemCallButtonClickListener listener){
        this.callButtonClickListener = listener;
    }

    public  interface onShelterItemTrackButtonClickListener {
        void onTrackButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnTrackButtonClickListener(onShelterItemTrackButtonClickListener listener){
        this.trackButtonClickListener = listener;
    }

//    public interface onShelterItemClickListener{
//        //        void onFoodItemClick(DocumentSnapshot documentSnapshot, int position);
//        void onShelterItemClick(DocumentSnapshot documentSnapshot, int position);
//    }
//
//    public void setOnShelterItemClickListener(ShelterAdapter.onShelterItemClickListener listener){
//        this.listener = listener;
//    }

}
