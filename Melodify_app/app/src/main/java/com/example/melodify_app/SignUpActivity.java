package com.example.melodify_app;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import java.lang.ref.Reference;

public class SignUpActivity extends Activity {
    EditText signup_name, signup_email, signup_password, signup_birth;
    TextView loginRedirection; // TODO adaugat in xml
    Button signup_button;
    TextView redirect_login;

    FirebaseAuth fb_auth;
//    Firebase database;
//    Reference reference;
//    FirebaseDatabase database; // TODO schimbat in FirebaseDatabase??
//    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        fb_auth = FirebaseAuth.getInstance();

        signup_name = findViewById(R.id.signup_name);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        signup_birth = findViewById(R.id.signup_birth);

        redirect_login=findViewById(R.id.redirect_login);

        redirect_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                database = Firebase.getInstance();
//                reference= database.getReference("users");

                String name = signup_name.getText().toString();
                String email = signup_email.getText().toString();
                String password = signup_password.getText().toString();
                String birth = signup_birth.getText().toString();

                //din documentatie de la firebase
                fb_auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = fb_auth.getCurrentUser();
//                                    updateUI(user);

                                    Toast.makeText(SignUpActivity.this, "Account created successfully!",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Account creation failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                            }
                        });

            };

        });
    }
}