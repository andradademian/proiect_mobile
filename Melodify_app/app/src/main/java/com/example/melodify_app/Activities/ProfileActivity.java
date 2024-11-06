package com.example.melodify_app.Activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.Model_Auxiliare.ProjectCard;
import com.example.melodify_app.Model_Auxiliare.ProjectCardAdapter;
import com.example.melodify_app.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // Sample data
        List<ProjectCard> cardDataList = new ArrayList<>();
        cardDataList.add(new ProjectCard("Title 1", "Description for card 1"));
        cardDataList.add(new ProjectCard("Title 2", "Description for card 2"));
        cardDataList.add(new ProjectCard("Title 3", "Description for card 3"));
//        cardDataList.add(new ProjectCard("Title 4", "Description for card 4"));
//        cardDataList.add(new ProjectCard("Title 5", "Description for card 5"));
//        cardDataList.add(new ProjectCard("Title 6", "Description for card 6"));
//        cardDataList.add(new ProjectCard("Title 7", "Description for card 7"));
//        cardDataList.add(new ProjectCard("Title 8", "Description for card 8"));
//        cardDataList.add(new ProjectCard("Title 9", "Description for card 9"));

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProjectCardAdapter(cardDataList));
    }
}
