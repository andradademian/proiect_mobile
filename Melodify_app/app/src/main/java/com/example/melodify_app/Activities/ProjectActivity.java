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
        // Get the ProjectCard and construct the project ID
        ProjectCard project = (ProjectCard) getIntent().getSerializableExtra("CARD");
        String projectID = project.getUser().getEmail() + "_" + project.getTitle(); // Construct project_id
        for (Lyrics lyric : lyricsCards) {
            String text = lyric.getText();
            if (!text.trim().isEmpty()) { // Only save if the lyric has content
                // Set the project_id in the Lyrics object
                lyric.setProjectID(projectID);
                Integer index;
                if(lyric.getIndex()==null || lyric.getIndex()==0){
                     index= findIndexPosition(projectID);
                }
                else{
                    index= lyric.getIndex();
                }
                Log.d("IndexDebug", "Index value: " + index);
                // Create a HashMap to explicitly include the fields to save
                Map<String, Object> lyricData = new HashMap<>();
                lyricData.put("text", lyric.getText());
                lyricData.put("projectID", projectID); // Add project_id as a field
                lyricData.put("index", index); // Add project_id as a field
               // lyricData.put("timestamp", System.currentTimeMillis()); // Optional: add a timestamp

                // Save to Firestore with explicit document ID
                db.collection("project_component")
                        .document(projectID+"_"+index) // Unique ID for the lyric
                        .set(lyricData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Lyrics saved for project: " + projectID);
                        })
                        .addOnFailureListener(e -> Log.e("Firestore Error", "Error saving lyrics: ", e));
            }
        }

        Toast.makeText(ProjectActivity.this, "Lyrics saved!", Toast.LENGTH_SHORT).show();

        // Reload lyrics after saving
        loadLyricsFromDatabase(projectID);
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
