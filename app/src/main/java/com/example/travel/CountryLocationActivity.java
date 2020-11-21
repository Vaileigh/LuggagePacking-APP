package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.travel.Model.Location;
import com.example.travel.ViewHolder.LocationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CountryLocationActivity extends AppCompatActivity {

    private DatabaseReference locationRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_location);
        RelativeLayout relativeLayout= findViewById(R.id.relative_location_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Intent countryIntent = getIntent();
        String countryCode_string = countryIntent.getStringExtra("countryCode");
        Toast.makeText(CountryLocationActivity.this, countryCode_string, Toast.LENGTH_SHORT).show();

        locationRef = FirebaseDatabase.getInstance().getReference().child(countryCode_string);

        recyclerView = findViewById(R.id.recycler_location);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Location> options =
                new FirebaseRecyclerOptions.Builder<Location>()
                .setQuery(locationRef, Location.class)
                .build();

        FirebaseRecyclerAdapter<Location, LocationViewHolder> adapter =
                new FirebaseRecyclerAdapter<Location, LocationViewHolder>(options) {
                    @NonNull
                    @Override
                    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.location_layout,parent, false);
                        LocationViewHolder holder = new LocationViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull LocationViewHolder holder, int position, @NonNull Location model) {
                        final String locationName = model.getLocationName();
                        final String locationImage = model.getLocationImage();

                        holder.locationName.setText(locationName);
                        Picasso.get().load(locationImage).into(holder.locationImage);
                        //Location click listener
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CountryLocationActivity.this, TravelInfoActivity.class);
                                intent.putExtra("locationName", locationName);
                                intent.putExtra("locationImage",locationImage);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void previous(View view) {
        this.finish();
    }
}
