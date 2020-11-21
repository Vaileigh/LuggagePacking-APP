package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {

    String user_id;
    CheckBox btn_baby;
    CheckBox btn_menstrual;
    CheckBox btn_wheelChair;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        RelativeLayout relativeLayout= findViewById(R.id.relative_filter_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        btn_baby = (CheckBox) findViewById(R.id.baby_checkBox);
        btn_menstrual = (CheckBox) findViewById(R.id.menstrual_checkBox);
        btn_wheelChair = (CheckBox) findViewById(R.id.wheelChair_checkBox);

        FirebaseUser mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
        user_id = mFirebaseUser.getUid();

    }

    public void previous(View view) {
        finish();
    }

    public void addFilter(View view) {

        if(btn_baby.isChecked()){

            db.collection("Filter_Baby")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(final QueryDocumentSnapshot babyDoc: task.getResult()){
                            Map<String,Object> newItem = new HashMap<>();
                            newItem.put("item", babyDoc.getString("item"));
                            newItem.put("qty",0);
                            newItem.put("category","baby");

                            db.collection(user_id).document(babyDoc.getId())
                                    .set(newItem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("Baby "+babyDoc.getId(), "Add Successful");
                                        }
                                    });
                        }
                    }
                }
            });

        }if(btn_menstrual.isChecked()){
            db.collection("Filter_Menstrual")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(final QueryDocumentSnapshot menstrualDoc: task.getResult()){
                            Map<String,Object> newItem = new HashMap<>();
                            newItem.put("item", menstrualDoc.getString("item"));
                            newItem.put("qty",0);
                            newItem.put("category","menstrual");

                            db.collection(user_id).document(menstrualDoc.getId())
                                    .set(newItem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("Menstrual "+menstrualDoc.getId(), "Add Successful");
                                        }
                                    });
                        }
                    }
                }
            });

        }if(btn_wheelChair.isChecked()){

            db.collection("Filter_Senior")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(final QueryDocumentSnapshot seniorDoc: task.getResult()){
                            Map<String,Object> newItem = new HashMap<>();
                            newItem.put("item", seniorDoc.getString("item"));
                            newItem.put("qty",0);
                            newItem.put("category","menstrual");

                            db.collection(user_id).document(seniorDoc.getId())
                                    .set(newItem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("Menstrual "+seniorDoc.getId(), "Add Successful");
                                        }
                                    });
                        }
                    }
                }
            });

        }
        finish();
    }
}
