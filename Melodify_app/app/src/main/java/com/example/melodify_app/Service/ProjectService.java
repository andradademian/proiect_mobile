package com.example.melodify_app.Service;

import androidx.annotation.NonNull;

import com.example.melodify_app.Model_Auxiliare.Project;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProjectService implements Service<Project>{

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void save(Project project) {
        db.collection("projects")
                .add(project)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(activity.this, "Welcome!",
//                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SignUpActivity.this, "Error creating user!",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void getAll() {

    }

    @Override
    public void getById() {

    }

    @Override
    public void delete() {

    }
}
