package com.example.melodify_app.Service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.melodify_app.Model_Auxiliare.PasswordHash;
import com.example.melodify_app.Model_Auxiliare.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserService implements Service<User> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void save(User user) {
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(SignUpActivity.this, "Welcome!",
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

    public void update(String email, String newName, String newPassword) {
        // Hash the new password
        String hashedPassword = PasswordHash.hashPassword(newPassword);

        // Update the Firestore document
        db.collection("users").document(email)
                .update("name", newName, "password", hashedPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("update", "User successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("update", "Error updating user", e);
                    }
                });
    }




    @Override
    public void getAll() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
//                                    Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void getById() {

    }
    @Override
    public User getUserByEmail(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Access the collection and get the document by email (used as the ID)
        db.collection("users")
                .document(email)  // Assuming email is used as the document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Deserialize the document into a User object
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            Log.d("getUserByEmail", "User found: " + user.toString());
                        } else {
                            Log.d("getUserByEmail", "User object is null");
                        }
                    } else {
                        Log.d("getUserByEmail", "No user found with this email");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("getUserByEmail", "Error fetching user", e);
                });
        return null;
    }


    @Override
    public void delete() {

    }
}
