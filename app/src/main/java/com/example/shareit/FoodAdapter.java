package com.example.shareit;
import android.app.AlertDialog;
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

public class FoodAdapter extends FirestoreRecyclerAdapter<FoodItem, FoodAdapter.FoodViewHolder> {

//    private onFoodItemClickListener listener;
    private onFoodItemMessageButtonClickListener messageButtonClickListener;
    private onFoodItemCallButtonClickListener callButtonClickListener;
    private onFoodItemTrackButtonClickListener trackButtonClickListener;
    Context context;

    public FoodAdapter(@NonNull FirestoreRecyclerOptions<FoodItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodcard_item, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if(getItemCount() == 0){

        }
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull FoodItem model) {

        holder.DonorName.setText(String.valueOf(model.DonorName));
        holder.DonorNumber.setText(String.valueOf(model.DonorNumber));
        holder.FoodName.setText(String.valueOf(model.FoodName));
        holder.FoodCount.setText(String.valueOf(model.FoodCount));
        if(model.getVerification()){
            holder.donor_verification.setVisibility(View.VISIBLE);
        }

    }

     class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView DonorName, DonorNumber, FoodName, FoodCount;
        Button send_message, call_donor, track_location;
        ImageView donor_verification;
        CardView cardView;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            DonorName = itemView.findViewById(R.id.foodDonorName);
            DonorNumber = itemView.findViewById(R.id.foodDonorNumber);
            FoodName = itemView.findViewById(R.id.foodItemName);
            FoodCount = itemView.findViewById(R.id.foodItemCount);
            cardView = itemView.findViewById(R.id.foodItemCard);
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
//                        listener.onFoodItemClick(getSnapshots().getSnapshot(position), position);
//                    }
//                }
//            });

        }
    }

    public  interface onFoodItemMessageButtonClickListener {
        void onMessageButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnMessageButtonClickListener(onFoodItemMessageButtonClickListener listener){
        this.messageButtonClickListener = listener;
    }

    public  interface onFoodItemCallButtonClickListener {
        void onCallButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnCallButtonClickListener(onFoodItemCallButtonClickListener listener){
        this.callButtonClickListener = listener;
    }

    public  interface onFoodItemTrackButtonClickListener {
        void onTrackButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnTrackButtonClickListener(onFoodItemTrackButtonClickListener listener){
        this.trackButtonClickListener = listener;
    }


//    public interface onFoodItemClickListener{
//        void onFoodItemClick(DocumentSnapshot documentSnapshot, int position);
//    }
//
//    public void setOnFoodItemClickListener(onFoodItemClickListener listener){
//        this.listener = listener;
//    }

}
