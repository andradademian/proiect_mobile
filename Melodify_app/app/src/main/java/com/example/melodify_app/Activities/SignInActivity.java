package com.example.melodify_app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.melodify_app.Model_Auxiliare.PasswordHash;
import com.example.melodify_app.Model_Auxiliare.User;
import com.example.melodify_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends Activity {
    EditText login_adress, login_password;
    Button login_button;
    TextView redirect_register;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        db = FirebaseFirestore.getInstance();

        login_adress = findViewById(R.id.login_adress);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        redirect_register = findViewById(R.id.redirect_register);

        redirect_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_adress.getText().toString();
                String password = login_password.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve the user document by email
                db.collection("users")
                        .document(email)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                if (document.exists()) {
                                    String storedHashedPassword = document.getString("password");

                                    // Check if the entered password matches the stored hashed password
                                    if (PasswordHash.checkPassword(password, storedHashedPassword)) {
                                        Toast.makeText(SignInActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();

                                        User user = new User (document.getString("name"), document.getString("email"), document.getString("password"));
                                        Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
                                        intent.putExtra("USER", user);

                                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("name", user.getName());
                                        editor.putString("email", user.getEmail());
                                        editor.putString("password", user.getPassword());
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();

                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Wrong login credentials!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SignInActivity.this, "No account found with this email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignInActivity.this, "Error logging in!", Toast.LENGTH_SHORT).show();
                                Log.e("LOGIN_ERROR", "Error: ", e);
                            }
                        });
            }
        });
    }
}
