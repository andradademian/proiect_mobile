package com.example.melodify_app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.example.melodify_app.Model_Auxiliare.User;
import com.example.melodify_app.R;

public class StartingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_layout);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    String name = sharedPreferences.getString("name", "");
                    String email = sharedPreferences.getString("email", "");
                    String password = sharedPreferences.getString("password", "");
                    User user = new User(name, email, password);
                    Intent intent = new Intent(StartingActivity.this, ProfileActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(StartingActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}
