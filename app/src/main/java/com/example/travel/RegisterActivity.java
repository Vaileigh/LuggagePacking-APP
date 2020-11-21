package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore db;
    boolean pwdBool=false;
    boolean emailBool=false;
    private String emailReg_string;
    private String pwdReg_string;
    private String coPwdReg_string;

    EditText emailReg,pwdReg,coPwdReg;
    Button btnReg;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RelativeLayout relativeLayout= findViewById(R.id.relative_register_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailReg = findViewById(R.id.registerEmail_editText);
        pwdReg = findViewById(R.id.registerPassword_editText);
        coPwdReg = findViewById(R.id.reenterRegisterPassword_editText);
        btnReg = findViewById(R.id.reg_button);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailBool=false;
                pwdBool=false;
                emailReg_string = emailReg.getText().toString();
                pwdReg_string = pwdReg.getText().toString();
                coPwdReg_string = coPwdReg.getText().toString();
                emailValidate();
                passwordValidate();
                if(pwdBool&&emailBool){

                    mFirebaseAuth.createUserWithEmailAndPassword(emailReg_string,pwdReg_string)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        emailReg.setError("Use other email!");
                                        emailReg.requestFocus();
                                        Toast.makeText
                                                (RegisterActivity.this, "This account has been registered!", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    else{

                                        mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText
                                                            (RegisterActivity.this, "Register successful!\nPlease check your email for verification!", Toast.LENGTH_SHORT)
                                                            .show();

                                                    String uid = mFirebaseAuth.getUid();
                                                    Map<String,Object> newUser = new HashMap<>();
                                                    newUser.put("email", emailReg_string);
                                                    newUser.put("password", pwdReg_string);
                                                    newUser.put("login",0);
                                                    db.collection("Users").document(uid)
                                                            .set(newUser)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("ERROR",e.getMessage());
                                                                }
                                                            });
                                                    startActivity(new Intent(RegisterActivity.this, IdentityActivity.class));
                                                }
                                                else{
                                                    Toast.makeText
                                                            (RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }


    private void emailValidate() {
        emailBool= false;
        emailReg_string = emailReg.getText().toString();
        if(emailReg_string.equals("")){
            emailReg.setError("Email required!");
            emailReg.requestFocus();
        }
        else{
            String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            if(emailReg_string.matches(regex)){
                emailBool=true;
            }
            else{
                emailReg.setError("Email incorrect format!");
                emailReg.requestFocus();
            }

        }
    }

    public void passwordValidate(){
        pwdBool =false;
        pwdReg_string = pwdReg.getText().toString();
        coPwdReg_string = coPwdReg.getText().toString();
        if (pwdReg_string.equals(coPwdReg_string)){
            if(pwdReg_string.equals("")){
                pwdReg.setError("Password required!");
                pwdReg.requestFocus();
            }
            else{
                if(pwdReg_string.length()>7){
                    pwdBool = true;
                }
                else{
                    pwdReg.setError("Password must be 8 character or longer!");
                    pwdReg.requestFocus();
                }

            }
        }
        else{
            if(pwdReg_string.equals("")){
                pwdReg.setError("Password required!");
                pwdReg.requestFocus();
            }
            else if(coPwdReg_string.equals("")){
                coPwdReg.setError("Re-enter password required!");
                coPwdReg.requestFocus();
            }
            else{
                coPwdReg.setError("Re-enter password not match!");
                pwdReg.setError("Password not match!");
                pwdReg.requestFocus();
                coPwdReg.requestFocus();
            }
        }
    }
}
