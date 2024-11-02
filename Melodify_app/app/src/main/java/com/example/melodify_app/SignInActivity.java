package com.example.melodify_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class SignInActivity extends Activity {
    EditText login_adress, login_password;
    Button loin_button;
    TextView redirect_register;
    FirebaseAuth fb_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        fb_auth = FirebaseAuth.getInstance();

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

        loin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                database = Firebase.getInstance();
//                reference= database.getReference("users");

                String email = login_adress.getText().toString();
                String password = login_password.getText().toString();



                //din documentatie de la firebase
                fb_auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = fb_auth.getCurrentUser();
//                                    updateUI(user);

                                    Toast.makeText(SignInActivity.this, "Welcome!",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                            }
                        });

            };

        });
    }
}
