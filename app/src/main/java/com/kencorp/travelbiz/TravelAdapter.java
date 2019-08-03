package com.kencorp.travelbiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.kencorp.travelbiz.FirebaseUtil.mDatabaseReference;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.DealViewHolder>{

    ArrayList<TravelDeals> dealsArrayList ;

    private FirebaseDatabase mFirebaseDatabase;

    private ChildEventListener mChildEventListener ;

    private DatabaseReference mDatabaseReference ;

    public TravelAdapter(MainActivity activity) {


        FirebaseUtil.openFirebaseReference("traveldeals",  activity);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        dealsArrayList = FirebaseUtil.dealsArrayList;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        mChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               TravelDeals travelDeals= dataSnapshot.getValue(TravelDeals.class);

              //  dataSnapshot.getValue(TravelDeals.class);


                travelDeals.setId(dataSnapshot.getKey());

                Log.d("TravelAdapter", travelDeals.getDescription());
                dealsArrayList.add(travelDeals);

                notifyItemInserted(dealsArrayList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        mDatabaseReference.addChildEventListener(mChildEventListener);
    }


    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deals,parent,false);

        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {

        TravelDeals travelDeals = dealsArrayList.get(position);

        Log.d("TravelAdapter", travelDeals.getPrice());

        holder.bind(travelDeals);

    }

    @Override
    public int getItemCount() {
        return dealsArrayList.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder {
           // implements View.OnClickListener{

        private TextView txtTitle;
        private TextView txtDescription;
        private TextView txtPrice;

        private ImageView imageView;

        private View view;


        public DealViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            txtTitle = itemView.findViewById(R.id.txtTitle);

            txtPrice = itemView.findViewById(R.id.txtPrice);

            txtDescription = itemView.findViewById(R.id.txtDescription);

            imageView = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Log.d("on DealViewHolder",String.valueOf(position));

                    TravelDeals selectTravelDeals = dealsArrayList.get(position);

                      Intent intent = new Intent(v.getContext(),DealActivity.class);


                   // Intent intent = new Intent(v.getContext(),InsertActivity.class);

                    intent.putExtra("Deal",selectTravelDeals);

                    v.getContext().startActivity(intent);


                }
            });
        }


        public void bind(TravelDeals travelDeals) {

            Log.d("TravelAdapter", travelDeals.getTitle());

            txtTitle.setText(travelDeals.getTitle().toUpperCase());
            txtDescription.setText(travelDeals.getDescription());
            txtPrice.setText(travelDeals.getPrice());

            if(travelDeals.getImageUrl() != null && !travelDeals.getImageUrl().isEmpty())
            {
                Picasso.with(view.getContext())
                        .load(travelDeals.getImageUrl())
                        .centerCrop()
                        .resize(100,100)
                        .into(imageView)
                ;
            }
        }

       /* @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            Log.d("on DealViewHolder",String.valueOf(position));

            TravelDeals selectTravelDeals = dealsArrayList.get(position);

          //  Intent intent = new Intent(v.getContext(),DealActivity.class);


            Intent intent = new Intent(v.getContext(),InsertActivity.class);

            intent.putExtra("Deal",selectTravelDeals);

            v.getContext().startActivity(intent);

        }*/
    }
}
