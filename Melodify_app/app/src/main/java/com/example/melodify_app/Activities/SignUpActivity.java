package com.example.melodify_app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends Activity {
    EditText signup_name;
    EditText signup_email;
    EditText signup_password;
    String hashed_password;
//    EditText signup_birth;
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
//        signup_birth = findViewById(R.id.signup_birth);

        redirect_login=findViewById(R.id.redirect_login);

        redirect_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signup_button=findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signup_name.getText().toString();
                String email = signup_email.getText().toString();
                String password = signup_password.getText().toString();
//                String birth = signup_birth.getText().toString();

                String hashedPassword = PasswordHash.hashPassword(password);


                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

//                User user= new User(name,email,password,birth);
                User user= new User(name,email,hashedPassword);

                //TODO sa nu adaugi de mai multe ori acceasei chestie in db

                // Add a new document with a generated ID
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
//                                Toast.makeText(SignUpActivity.this, "You can login now!",
                                Toast.makeText(SignUpActivity.this, "Welcome!",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Error creating user!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            };

        });
    }
}