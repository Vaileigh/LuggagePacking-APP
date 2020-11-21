package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifiedActivity extends AppCompatActivity {

    TextView email;
    Button btnSend;
    FirebaseAuth mFirebaseAuth;
    private String email_string, pwd_string;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);
        RelativeLayout relativeLayout= findViewById(R.id.relative_verified_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Intent intent = getIntent();
        email_string = intent.getStringExtra("email");
        pwd_string = intent.getStringExtra("password");

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.textView);
        btnSend = findViewById(R.id.verified_button);

        email.setText(email_string);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mFirebaseUser != null){
                    Toast.makeText(VerifiedActivity.this,"Logged in as "+email_string+" !",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifiedActivity.this, MenuActivity.class));
                }
                else{
                    Toast.makeText(VerifiedActivity.this,"Please Login!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signInWithEmailAndPassword(email_string, pwd_string)
                        .addOnCompleteListener(VerifiedActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    email.requestFocus();
                                    Toast.makeText
                                            (VerifiedActivity.this, "Email or Password Incorrect!", Toast.LENGTH_SHORT)
                                            .show();
                                    Log.i("Resend verified Email", task.getException().getMessage());
                                }
                                else{
                                    if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                        Toast.makeText
                                                (VerifiedActivity.this, email_string+"\nhas been verified!", Toast.LENGTH_SHORT)
                                                .show();
                                        finish();
                                    }
                                    else{
                                        mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText
                                                            (VerifiedActivity.this, "Please check your email below:\n"+email_string, Toast.LENGTH_SHORT)
                                                            .show();
                                                    finish();
                                                }
                                                else{
                                                    Log.i("Resend verified Email", task.getException().getMessage());
                                                }
                                            }
                                        });

                                    }

                                }
                            }
                        });
            }
        });
    }

}
