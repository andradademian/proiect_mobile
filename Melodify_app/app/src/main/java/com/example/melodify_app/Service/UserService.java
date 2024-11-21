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
    private static UserService instance; // Singleton instance
    private final FirebaseFirestore db; // Firebase instance

    // Private constructor to prevent instantiation from other classes
    private UserService() {
        db = FirebaseFirestore.getInstance();
    }

    // Public method to provide access to the singleton instance
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public void save(User user) {
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("save", "User successfully added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("save", "Error adding user", e);
                    }
                });
    }

    public void update(String email, String newName, String newPassword) {
        String hashedPassword = PasswordHash.hashPassword(newPassword);

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
                                Log.d("getAll", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("getAll", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void getById() {
        // Implementation can be added here
    }

    public User getUserByEmail(String email) {
        db.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
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
        // Implementation can be added here
    }
}
