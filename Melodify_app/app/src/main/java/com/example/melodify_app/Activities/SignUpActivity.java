package com.example.melodify_app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.melodify_app.Model_Auxiliare.PasswordHash;
import com.example.melodify_app.R;
import com.example.melodify_app.Model_Auxiliare.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends Activity {
    EditText signup_name;
    EditText signup_email;
    EditText signup_password;
    Button signup_button;
    TextView redirect_login;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        db = FirebaseFirestore.getInstance();

        signup_name = findViewById(R.id.signup_name);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        redirect_login = findViewById(R.id.redirect_login);

        redirect_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signup_button = findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signup_name.getText().toString();
                String email = signup_email.getText().toString();
                String password = signup_password.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String hashedPassword = PasswordHash.hashPassword(password);

                // Check if the email already exists in Firestore
                db.collection("users").document(email).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                if (document.exists()) {
                                    Toast.makeText(SignUpActivity.this, "Email is already registered!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If email doesn't exist, save the new user
                                    User user = new User(name, email, hashedPassword);
                                    db.collection("users").document(email).set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SignUpActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignUpActivity.this, "Error creating user!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Error checking email!", Toast.LENGTH_SHORT).show();
                                Log.e("SIGNUP_ERROR", "Error: ", e);
                            }
                        });
            }
        });
    }
}
