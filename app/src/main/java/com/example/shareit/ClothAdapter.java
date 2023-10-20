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

public class ClothAdapter extends FirestoreRecyclerAdapter<ClothItem, ClothAdapter.ClothViewHolder> {

//    private onClothItemClickListener listener;
    Context context;
    private onClothItemMessageButtonClickListener messageButtonClickListener;
    private onClothItemCallButtonClickListener callButtonClickListener;
    private onClothItemTrackButtonClickListener trackButtonClickListener;

    public ClothAdapter(@NonNull FirestoreRecyclerOptions<ClothItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public ClothAdapter.ClothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.clothcard_item, parent, false);
        return new ClothViewHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0){

        }
//            Toast.makeText(context, "There are no clothes available at this moment", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onBindViewHolder(@NonNull ClothViewHolder holder, int position, @NonNull ClothItem model) {

        holder.DonorName.setText(String.valueOf(model.DonorName));
        holder.DonorNumber.setText(String.valueOf(model.DonorNumber));
        holder.ClothName.setText(String.valueOf(model.ClothName));
        holder.ClothCount.setText(String.valueOf(model.ClothCount));
        if(model.getVerification()){
            holder.donor_verification.setVisibility(View.VISIBLE);
        }
    }

    class ClothViewHolder extends RecyclerView.ViewHolder {

        TextView DonorName, DonorNumber, ClothName, ClothCount;
        CardView cardView;
        Button send_message, call_donor, track_location;
        ImageView donor_verification;
        public ClothViewHolder(@NonNull View itemView) {
            super(itemView);
            DonorName = itemView.findViewById(R.id.clothDonorName);
            DonorNumber = itemView.findViewById(R.id.clothDonorNumber);
            ClothName = itemView.findViewById(R.id.clothItemName);
            ClothCount = itemView.findViewById(R.id.clothItemCount);
            cardView = itemView.findViewById(R.id.clothItemCard);
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
//                        listener.onClothItemClick(getSnapshots().getSnapshot(position), position);
//                    }
//                }
//            });

        }
    }

    public  interface onClothItemMessageButtonClickListener {
        void onMessageButtonClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnMessageButtonClickListener(onClothItemMessageButtonClickListener listener){
        this.messageButtonClickListener = listener;
    }

    public  interface onClothItemCallButtonClickListener {
        void onCallButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnCallButtonClickListener(onClothItemCallButtonClickListener listener){
        this.callButtonClickListener = listener;
    }

    public  interface onClothItemTrackButtonClickListener {
        void onTrackButtonClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnTrackButtonClickListener(onClothItemTrackButtonClickListener listener){
        this.trackButtonClickListener = listener;
    }

//    public interface onClothItemClickListener{
//        void onClothItemClick(DocumentSnapshot documentSnapshot, int position);
//    }
//
//    public void setOnClothItemClickListener(onClothItemClickListener listener){
//        this.listener = listener;
//    }

}
