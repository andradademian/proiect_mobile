package com.example.melodify_app.Activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.melodify_app.R;

public class ProjectActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_layout);
    }
}
