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

    private Button edit_button, new_hit_button;
    private List<ProjectCard> cardDataList;
    private User user;

    private FirebaseFirestore db;
    private UserService userService;
    private ProjectService projectService;
    private ProjectCardAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // Initialize services
        db = FirebaseFirestore.getInstance();
        userService = UserService.getInstance();
        projectService = new ProjectService();

        // Initialize UI components
        edit_button = findViewById(R.id.edit_button);
        new_hit_button = findViewById(R.id.new_hit_button);
        recyclerView = findViewById(R.id.recycler_view);
        cardDataList = new ArrayList<>();

        // Retrieve user object
        user = (User) getIntent().getSerializableExtra("USER");
        TextView username = findViewById(R.id.username);
        username.setText(user.getName());

        // Set up RecyclerView and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectCardAdapter(cardDataList);
        recyclerView.setAdapter(adapter);

        // Load projects for the user
        loadProjects();

        // Set button listeners
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        new_hit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewProjectDialog();
            }
        });
    }

    /**
     * Fetches projects for the user from Firestore and updates the RecyclerView.
     */
    private void loadProjects() {
        db.collection("projects")
                .whereEqualTo("userID", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            cardDataList.clear(); // Clear old data
                            for (DocumentSnapshot document : querySnapshot) {
                                String projectName = document.getString("name");
                                String projectDescription = document.getString("description");
                                ProjectCard projectCard = new ProjectCard(projectName, projectDescription);
                                cardDataList.add(projectCard);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
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

                // Update username on the screen
                TextView username = findViewById(R.id.username);
                username.setText(user.getName());
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

        View view = getLayoutInflater().inflate(R.layout.new_hit_layout, null);
        builder.setView(view);

        EditText hit_title = view.findViewById(R.id.hit_title);
        EditText hit_description = view.findViewById(R.id.hit_description);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String hTitle = hit_title.getText().toString();
            String hDescription = hit_description.getText().toString();

            if (!hTitle.isEmpty()) {
                Project newHit = new Project(hTitle, hDescription, user.getEmail());
                db.collection("projects")
                        .document(newHit.getUserID() + "_" + newHit.getName())
                        .set(newHit)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ProfileActivity.this,
                                        "New Song added!", Toast.LENGTH_SHORT).show();
                                cardDataList.add(new ProjectCard(newHit.getName(), newHit.getDescription()));
                                adapter.notifyDataSetChanged(); // Update RecyclerView

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
