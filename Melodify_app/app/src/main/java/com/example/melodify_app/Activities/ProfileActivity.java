package com.example.melodify_app.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.Model_Auxiliare.ProjectCard;
import com.example.melodify_app.Model_Auxiliare.ProjectCardAdapter;
import com.example.melodify_app.Model_Auxiliare.User;
import com.example.melodify_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        db = FirebaseFirestore.getInstance();

        User user = (User) getIntent().getSerializableExtra("USER");

        TextView username = findViewById(R.id.username);

        username.setText(user.getName());

        Query query = db.collection("projects")
                .whereEqualTo("user_id", user.getEmail());

        // Sample data
        List<ProjectCard> cardDataList = new ArrayList<>();

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot document : querySnapshot) {
//                        String projectId = document.getId();
                        String projectName = document.getString("project_name");  // Assuming there's a 'project_name' field
//                        String userId = document.getString("user_id");  // Assuming there's a 'user_id' field
                        String projectDescription = document.getString("description");  // Assuming there's a 'user_id' field

                        // Create a ProjectCard and add it to the list
//                        ProjectCard projectCard = new ProjectCard(projectId, projectName, projectDescription);
                        ProjectCard projectCard = new ProjectCard(projectName, projectDescription);
                        cardDataList.add(projectCard);
                    }
                    System.out.println("Projects retrieved: " + cardDataList.size());
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

//        cardDataList.add(new ProjectCard("Title 8", "Description for card 8"));
//        cardDataList.add(new ProjectCard("Title 9", "Description for card 9"));

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProjectCardAdapter(cardDataList));

    }
}
