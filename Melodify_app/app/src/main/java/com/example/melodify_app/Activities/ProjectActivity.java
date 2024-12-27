package com.example.melodify_app.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.Model_Auxiliare.AudioFile;
import com.example.melodify_app.Model_Auxiliare.Lyrics;
import com.example.melodify_app.Model_Auxiliare.LyricsAdapter;
import com.example.melodify_app.Model_Auxiliare.ProjectCard;
import com.example.melodify_app.Model_Auxiliare.RegistrationCardAdapter;
import com.example.melodify_app.Model_Auxiliare.SpaceItemDecoration;
import com.example.melodify_app.Model_Auxiliare.User;
import com.example.melodify_app.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.firestore.WriteBatch;

public class ProjectActivity extends Activity {
    User user;

    private Button stopRecordingButton;
    List<Lyrics> lyricsCards;
    List<AudioFile> registrationCards;

    private Button addRecordingButton;
    private Button addLyricsButton;
    private Button saveSongButton;  // Added Save Song Button
    private ImageButton playRecordingButton;
    private ImageButton pinButton;
    private ImageButton backButton;

    private RecyclerView recyclerViewLyrics;
    private LyricsAdapter lyricsAdapter;
    private MediaRecorder mediaRecorder;
    private String filePath;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1000;

    private FirebaseFirestore db;
    private CollectionReference lyricsCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_layout);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        lyricsCollection = db.collection("project_component");

        user = (User) getIntent().getSerializableExtra("USER");
        ProjectCard project = (ProjectCard) getIntent().getSerializableExtra("CARD");
        TextView songtitle = findViewById(R.id.textView3);
        songtitle.setText(project.getTitle());

        String projectID = project.getUser().getEmail() + "_" + project.getTitle();
        TextView songdescription = findViewById(R.id.song_description);
        songdescription.setText(project.getDescription());

        registrationCards = new ArrayList<>();
        lyricsCards = new ArrayList<>();

        // Initialize RecyclerView for Lyrics
        recyclerViewLyrics = findViewById(R.id.recycler_view);
        recyclerViewLyrics.setLayoutManager(new LinearLayoutManager(this));
        lyricsAdapter = new LyricsAdapter(lyricsCards);
        recyclerViewLyrics.setAdapter(lyricsAdapter);

        int spacing = 45; // Adjust the spacing as needed
        recyclerViewLyrics.addItemDecoration(new SpaceItemDecoration(spacing));

       // registrationCards.add(new AudioFile(""));  // Add data for registration cards

        RecyclerView recyclerViewRegistration = findViewById(R.id.recycler_registration);
        recyclerViewRegistration.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRegistration.setAdapter(new RegistrationCardAdapter(registrationCards));
        recyclerViewRegistration.addItemDecoration(new SpaceItemDecoration(spacing));

        addRecordingButton = findViewById(R.id.recordAdd);
        addLyricsButton = findViewById(R.id.textAdd);
        saveSongButton = findViewById(R.id.saveSong);  // Added Save Song Button
        backButton = findViewById(R.id.imageButton1);

        filePath = getExternalCacheDir().getAbsolutePath() + "/recording.3gp"; // Adjust path as needed

        addRecordingButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                startRecording();
                addRecordingButton.setText("Stop Recording!");
            } else {
                requestPermissions();
            }
        });

        addLyricsButton.setOnClickListener(v -> {
            lyricsCards.add(new Lyrics("", projectID));
            lyricsAdapter.notifyItemInserted(lyricsCards.size() - 1);
            recyclerViewLyrics.scrollToPosition(lyricsCards.size() - 1);
        });

        saveSongButton.setOnClickListener(v -> saveLyricsToDatabase());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectActivity.this, ProfileActivity.class);
            intent.putExtra("USER", user);
            startActivity(intent);
            finish();
        });

        // Load saved lyrics from Firestore
        loadLyricsFromDatabase(projectID);
    }

    private Integer findIndexPosition(String projectId){
        int count=0;
        for( Lyrics lyrics: lyricsCards){
            if(lyrics.getProjectID().equals(projectId))
                count++;
        }
        return count;
    }
    private void saveLyricsToDatabase() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the ProjectCard and construct the project ID
        ProjectCard project = (ProjectCard) getIntent().getSerializableExtra("CARD");
        String projectID = project.getUser().getEmail() + "_" + project.getTitle(); // Construct project_id

        // Firestore batch write instance
        WriteBatch batch = db.batch();

        // 1. First, delete existing lyrics from the database for this project
        db.collection("project_component")
                .whereEqualTo("projectID", projectID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Check if there are existing lyrics for this project and delete them
                    for (DocumentSnapshot document : querySnapshot) {
                        batch.delete(document.getReference());
                    }

                    // 2. After deleting the existing lyrics, add new lyrics to the batch
                    for (int i = 0; i < lyricsCards.size(); i++) {
                        Lyrics lyric = lyricsCards.get(i);
                        String text = lyric.getText();

                        if (!text.trim().isEmpty()) { // Only save if the lyric has content
                            Integer index;

                            // Generate a unique index for each lyric object
                            if (lyric.getIndex() == null || lyric.getIndex() == 0) {
                                index = i + 1; // Use the loop index + 1 to generate a unique index
                            } else {
                                index = lyric.getIndex();
                            }

                            // Set the project ID and index on the lyric object
                            lyric.setProjectID(projectID);
                            lyric.setIndex(index);

                            // Create a HashMap to explicitly define fields
                            Map<String, Object> lyricData = new HashMap<>();
                            lyricData.put("text", lyric.getText());
                            lyricData.put("projectID", projectID);
                            lyricData.put("index", index);

                            // Define the Firestore document ID (unique for each lyric)
                            String documentID = projectID + "_" + index + "_" + System.currentTimeMillis(); // Add timestamp for uniqueness

                            // Add the lyric data to the batch
                            batch.set(db.collection("project_component").document(documentID), lyricData);

                            // Debug logging
                            Log.d("BatchDebug", "Added to batch: " + documentID);
                        }
                    }

                    // 3. Commit the batch write to Firestore
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Batch write successful");
                                Toast.makeText(ProjectActivity.this, "Lyrics saved!", Toast.LENGTH_SHORT).show();
                                // Reload lyrics after saving
                                loadLyricsFromDatabase(projectID);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore Error", "Batch write failed", e);
                                Toast.makeText(ProjectActivity.this, "Failed to save lyrics!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", "Failed to fetch existing lyrics", e);
                    Toast.makeText(ProjectActivity.this, "Error occurred while deleting existing lyrics!", Toast.LENGTH_SHORT).show();
                });
    }






    private void loadLyricsFromDatabase(String projectID) {
        db.collection("project_component")
                .whereEqualTo("projectID", projectID) // Filter by project_id
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    lyricsCards.clear(); // Clear existing data
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Lyrics lyric = document.toObject(Lyrics.class);
                        Log.d("ProjectActivity", document.toString());
                        Log.d("ProjectActivity", lyric.toString());

                        lyricsCards.add(lyric);
                    }
                    lyricsAdapter.notifyDataSetChanged(); // Update RecyclerView
                })
                .addOnFailureListener(e -> Log.e("Firestore Error", "Error fetching lyrics: ", e));
    }



    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void startRecording() {
        if (isRecording) {
            Toast.makeText(this, "Recording is already in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(filePath);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
