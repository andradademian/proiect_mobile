package com.example.melodify_app.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.Model_Auxiliare.Project;
import com.example.melodify_app.Model_Auxiliare.ProjectCard;
import com.example.melodify_app.Model_Auxiliare.ProjectCardAdapter;
import com.example.melodify_app.Model_Auxiliare.User;
import com.example.melodify_app.R;
import com.example.melodify_app.Service.ProjectService;
import com.example.melodify_app.Service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {

    Button edit_button, new_hit_button;
    List<ProjectCard> cardDataList;
    User user;

    FirebaseFirestore db;
    UserService userService;
    ProjectService projectService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        db = FirebaseFirestore.getInstance();
        userService = new UserService();
        projectService = new ProjectService();

        edit_button = findViewById(R.id.edit_button);
        new_hit_button = findViewById(R.id.new_hit_button);
        cardDataList = new ArrayList<>();

        user = (User) getIntent().getSerializableExtra("USER");
        TextView username = findViewById(R.id.username);
        username.setText(user.getName());

        db.collection("projects")
                .whereEqualTo("userID", user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot document : querySnapshot) {
//                        String projectId = document.getId();
                        String projectName = document.getString("name");  // Assuming there's a 'project_name' field
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

        new_hit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = etUsername.getText().toString();
            String newPassword = etPassword.getText().toString();

            if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                user.setName(newUsername);
                user.setPassword(newPassword);

                userService.update(user.getEmail(), user.getName(), user.getPassword());

                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                recreate();
            } else {
                Toast.makeText(ProfileActivity.this,
                        "Please enter both username and password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showNewProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Let's get it started!");

        //TODO stuff here to initialise a new hit

        View view = getLayoutInflater().inflate(R.layout.new_hit_layout, null);
        builder.setView(view);

        EditText hit_title = view.findViewById(R.id.hit_title);
        EditText hit_description = view.findViewById(R.id.hit_description);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String hTitle = hit_title.getText().toString();
            String hDescription = hit_description.getText().toString();

            if (!hTitle.isEmpty()) {
                // TODO redirect
                Project newHit = new Project(hTitle, hDescription, user.getEmail());
//                projectService.save(newHit);
                db.collection("projects")
                        .document(newHit.getUserID()+"_"+newHit.getName())
                        .set(newHit)
//                        .add(newHit)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this,
                                "New Song added!", Toast.LENGTH_SHORT).show();
                                cardDataList.add(new ProjectCard(newHit.getName(),newHit.getDescription()));
                                Intent intent = new Intent(ProfileActivity.this, ProjectActivity.class);
                                intent.putExtra("NEW_HIT", newHit);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Error creating project!",
                                Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

}
