package com.example.melodify_app;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.ktx.Firebase;

import java.lang.ref.Reference;

public class SignUpActivity extends Activity {
    EditText signup_name, signup_email, signup_password, signup_birth;
    TextView loginRedirection; // TODO adaugat in xml
    Button signup_button;
//    Firebase database;
//    Reference reference;
//    FirebaseDatabase database; // TODO schimbat in FirebaseDatabase??
//    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        signup_name=findViewById(R.id.signup_name);
        signup_email=findViewById(R.id.signup_email);
        signup_password=findViewById(R.id.signup_password);
        signup_birth=findViewById(R.id.signup_birth);
//        loginRedirection=findViewById(R.id.redirect_login)

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                database = Firebase.getInstance();
//                reference= database.getReference("users");

                String name= signup_name.getText().toString();
            }
        });

    }

}
