package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.travel.Model.Country;
import com.example.travel.ViewHolder.CountryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MenuActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference countryRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        RelativeLayout relativeLayout= findViewById(R.id.relative_menu_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mFirebaseUser != null){ }
                else{
                    Toast.makeText(MenuActivity.this,"Please Login!",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MenuActivity.this, IdentityActivity.class));
                    finish();
                }
            }
        };

        countryRef = FirebaseDatabase.getInstance().getReference().child("Country");

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
    }


    //LogOut Function
    public void logout(View view) {

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        myAlertBuilder.setTitle("Log Out");
        myAlertBuilder.setMessage("Do you want to log out\nTravel Packing Guide?");
        myAlertBuilder.setPositiveButton("Log Out", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
                        String email = mFirebaseUser.getEmail();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText
                                (MenuActivity.this, email+"\nLogged out", Toast.LENGTH_SHORT)
                                .show();
                        startActivity(new Intent(MenuActivity.this, IdentityActivity.class));
                        finish();
                    }
                });
        myAlertBuilder.setNegativeButton( "Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog.
                        Toast.makeText(getApplicationContext(), "Cancel",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        myAlertBuilder.show();
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            Toast.makeText(this, "Hope to see you next time!", Toast.LENGTH_SHORT)
                    .show();
            super.onBackPressed();
            finish();
        }
        else{
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "One more time to exit!", Toast.LENGTH_SHORT)
                    .show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }

    //Country Function
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Country> options =
                new FirebaseRecyclerOptions.Builder<Country>()
                .setQuery(countryRef, Country.class)
                .build();

        FirebaseRecyclerAdapter<Country, CountryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Country, CountryViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CountryViewHolder holder, int position, @NonNull Country model) {
                        Log.i("onBindViewHolder","Menu Activity");
                        holder.countryName.setText(model.getCountryName());
                        Picasso.get().load(model.getCountryImage()).into(holder.countryImage);
                        //Country click listener
                        final String countryCode = model.getCountryCode();
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MenuActivity.this, CountryLocationActivity.class);
                                intent.putExtra("countryCode",countryCode);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.country_layout, parent, false);
                        CountryViewHolder holder = new CountryViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
