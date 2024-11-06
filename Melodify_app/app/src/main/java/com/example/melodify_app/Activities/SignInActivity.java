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

import com.example.melodify_app.PasswordHash;
import com.example.melodify_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends Activity {
    EditText login_adress, login_password;
    String hashedPassword;
    Button login_button;
    TextView redirect_register;
//    FirebaseAuth fb_auth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

//        fb_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        login_adress = findViewById(R.id.login_adress);
        login_password = findViewById(R.id.login_password);
        redirect_register=findViewById(R.id.redirect_register);

        redirect_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login_button=findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String email=String.valueOf(login_adress.getText());
                String email = login_adress.getText().toString();
                String password = login_password.getText().toString();

                String hashedPassword = PasswordHash.hashPassword(password);


                db.collection("users")
                        .whereEqualTo("email", email)
                        .whereEqualTo("password", hashedPassword)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Toast.makeText(SignInActivity.this, "Welcome!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SignInActivity.this, "Wrong login credentials!",
                                            Toast.LENGTH_SHORT).show();
//                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                //din documentatie de la firebase
//                fb_auth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
////                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = fb_auth.getCurrentUser();
////                                    updateUI(user);
//
//                                    Toast.makeText(SignInActivity.this, "Welcome!",
//                                            Toast.LENGTH_SHORT).show();
//
//                                } else {
//                                    // If sign in fails, display a message to the user.
////                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
////                                    updateUI(null);
//                                }
//                            }
//                        });

            };

        });
    }
}
