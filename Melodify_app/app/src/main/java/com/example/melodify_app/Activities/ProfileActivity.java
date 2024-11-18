package com.example.melodify_app.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    Button edit_button, new_hit_button;
    List<ProjectCard> cardDataList;
    User user;

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        db = FirebaseFirestore.getInstance();
        edit_button = findViewById(R.id.edit_button);
        new_hit_button=findViewById(R.id.new_hit_button);
        cardDataList = new ArrayList<>();

        user = (User) getIntent().getSerializableExtra("USER");
        TextView username = findViewById(R.id.username);
        username.setText(user.getName());
        Query query = db.collection("projects")
                .whereEqualTo("user_id", user.getEmail());

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

                        ProjectCard projectCard = new ProjectCard(projectName, projectDescription);
                        cardDataList.add(projectCard);
                    }
                    System.out.println("Projects retrieved: " + cardDataList.size());
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });
        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProjectCardAdapter(cardDataList));

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
                username.setText(user.getName());
            }
        });

        new_hit_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showNewProjectDialog();
            }
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Change Username/Password");

        View view = getLayoutInflater().inflate(R.layout.edit_profile_layout, null);
        builder.setView(view);

        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);

        // Add the buttons for "Save" and "Cancel"
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = etUsername.getText().toString();
            String newPassword = etPassword.getText().toString();

            // Handle the logic to update the username or password here
            if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                //TODO SCHIMBAT ASTEA pt ca se da edit doar la instanta de user de aici
                user.setName(newUsername);
                user.setPassword(newPassword);
            } else {
                Toast.makeText(ProfileActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showNewProjectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Let's get it started!");

        //TODO stuff here to initialise a new hit

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

}
