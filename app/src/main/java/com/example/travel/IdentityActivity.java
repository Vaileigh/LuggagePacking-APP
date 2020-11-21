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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class IdentityActivity extends AppCompatActivity {

    EditText email, password;
    TextView validate;
    Button btnLogin;
    FirebaseAuth mFirebaseAuth;
    private String email_string, pwd_string;
    boolean pwdBool=false;
    boolean emailBool=false;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);
        RelativeLayout relativeLayout= findViewById(R.id.relative_identify_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_editText);
        password = findViewById(R.id.password_editText);
        validate = findViewById(R.id.emailVerification_textView);
        btnLogin = findViewById(R.id.login_button);
        email_string = email.getText().toString();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                String email = mFirebaseUser.getEmail();
                if(mFirebaseUser != null){
                    Toast.makeText(IdentityActivity.this,"Logged in as "+email+" !",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(IdentityActivity.this, MenuActivity.class));
                }
                else{
                    Toast.makeText(IdentityActivity.this,"Please Login!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidate();
                passwordValidate();
                if(pwdBool&&emailBool){
                    mFirebaseAuth.signInWithEmailAndPassword(email_string, pwd_string)
                            .addOnCompleteListener(IdentityActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        email.requestFocus();
                                        Toast.makeText
                                                (IdentityActivity.this, "Email or Password Incorrect!", Toast.LENGTH_SHORT)
                                                .show();
                                        validate.setVisibility(View.INVISIBLE);
                                    }
                                    else{
                                        if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            String uid = mFirebaseAuth.getUid();
                                            db.collection("Users").document(uid)
                                                    .update("login", FieldValue.increment(1))
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
                                            Toast.makeText
                                                    (IdentityActivity.this, "Logged in as\n"+email_string, Toast.LENGTH_SHORT)
                                                    .show();
                                            startActivity(new Intent(IdentityActivity.this, MenuActivity.class));
                                            finish();
                                        }
                                        else{
                                            Toast.makeText
                                                    (IdentityActivity.this, email_string+"\n Verified needed!\nPlease check your email !", Toast.LENGTH_SHORT)
                                                    .show();
                                            validate.setVisibility(View.VISIBLE);
                                            email.setEnabled(false);
                                            password.setEnabled(false);
                                        }

                                    }
                                }
                            });
                }
            }
        });
    }

    private void emailValidate() {
        emailBool= false;
        email_string = email.getText().toString();
        if(email_string.equals("")){
            email.setError("Email required!");
            email.requestFocus();
        }
        else{
            String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            if(email_string.matches(regex)){
                emailBool=true;
            }
            else{
                email.setError("Email incorrect format!");
                email.requestFocus();
            }

        }
    }
    public void passwordValidate(){
        pwdBool =false;
        pwd_string = password.getText().toString();
        if(pwd_string.equals("")){
            password.setError("Password required!");
            password.requestFocus();
        }
        else{
            if(pwd_string.length()>7){
                pwdBool = true;
            }
            else{
                password.setError("Password must be 8 character or longer!");
                password.requestFocus();
            }

        }
    }

    public void registerAcc(View view) {
        Intent register =
                new Intent(IdentityActivity.this, RegisterActivity.class);
        startActivity(register);
    }

    public void validate(View view) {
        validate.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(IdentityActivity.this, VerifiedActivity.class);
        intent.putExtra("email",email_string);
        intent.putExtra("password",pwd_string);
        startActivity(intent);

        email.setEnabled(true);
        password.setEnabled(true);
    }
}
