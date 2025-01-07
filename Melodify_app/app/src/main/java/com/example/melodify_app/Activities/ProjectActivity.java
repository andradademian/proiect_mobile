package com.example.melodify_app.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends Activity {
    User user;

    List<Lyrics> lyricsCards;
    List<AudioFile> registrationCards;

    private Button saveSongButton, addRecordingButton, addLyricsButton;
    private ImageButton playRecordingButton, pinButton;
    private ImageButton backButton, deleteHit;

    private RecyclerView recyclerViewLyrics, recyclerViewRegistration;
    private LyricsAdapter lyricsAdapter;
    private RegistrationCardAdapter recordAdapter;
    private MediaRecorder mediaRecorder;
    private String filePath;
    private boolean isRecording = false, pressed = true;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1000;

    private FirebaseFirestore db;
    FirebaseStorage storage;
    private CollectionReference lyricsCollection;
    String projectID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_layout);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        lyricsCollection = db.collection("project_component");

        user = (User) getIntent().getSerializableExtra("USER");
        ProjectCard project = (ProjectCard) getIntent().getSerializableExtra("CARD");
        TextView songtitle = findViewById(R.id.textView3);
        songtitle.setText(project.getTitle());

        projectID = project.getUser().getEmail() + "_" + project.getTitle();
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

        recyclerViewRegistration = findViewById(R.id.recycler_registration);
        recyclerViewRegistration.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRegistration.setAdapter(new RegistrationCardAdapter(registrationCards));
        recyclerViewRegistration.addItemDecoration(new SpaceItemDecoration(spacing));

        addRecordingButton = findViewById(R.id.recordAdd);
        addLyricsButton = findViewById(R.id.textAdd);
        saveSongButton = findViewById(R.id.saveSong);
        backButton = findViewById(R.id.imageButton1);
        deleteHit = findViewById(R.id.deleteHit);

        //filePath = getExternalCacheDir().getAbsolutePath() + "/recording.3gp"; // Adjust path as needed
        filePath = getExternalFilesDir(null).getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".mp3";

        addRecordingButton.setOnClickListener(v -> {
            boolean permis = checkPermissions();
            changeText(pressed);
            if (pressed) {
                if (permis) {
                    //Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
                    startRecording();
                } else {
                    //Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
                    requestPermissions();
                }
            } else {
                stopRecording();
            }
            pressed = !pressed;
        });

        addLyricsButton.setOnClickListener(v -> {
            lyricsCards.add(new Lyrics("", projectID));
            lyricsAdapter.notifyItemInserted(lyricsCards.size() - 1);
            recyclerViewLyrics.scrollToPosition(lyricsCards.size() - 1);
        });

        saveSongButton.setOnClickListener(v -> saveLyricsToDatabase());

        deleteHit.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("project_component")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String documentId = document.getId();
                            if (documentId.matches("^" + projectID + ".*$")) {
                                document.getReference().delete();
                            }
                        }
                    });
//                    .addOnFailureListener(e -> {
//                        System.out.println("Error getting documents: " + e.getMessage());
//                    });

            db.collection("projects")
                    .document(projectID)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show();
                    });

            Intent intent = new Intent(ProjectActivity.this, ProfileActivity.class);
            intent.putExtra("USER", user);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectActivity.this, ProfileActivity.class);
            intent.putExtra("USER", user);
            startActivity(intent);
            finish();
        });
        // Load saved lyrics from Firestore
        loadLyricsFromDatabase(projectID);
        loadAudioFiles();
    }

    private Integer findIndexPosition(String projectId) {
        int count = 0;
        for (Lyrics lyrics : lyricsCards) {
            if (lyrics.getProjectID().equals(projectId))
                count++;
        }
        return count;
    }

    private void changeText(boolean pressed) {
        if (pressed) {
            addRecordingButton.setText("Stop Recording");
        }
        if (!pressed) {
            addRecordingButton.setText("Add Recording");
        }
    }

    private void saveLyricsToDatabase() {
        // Get the ProjectCard and construct the project ID
        ProjectCard project = (ProjectCard) getIntent().getSerializableExtra("CARD");

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
                            String documentID = projectID + "_text_"
                                    + index + "_" + System.currentTimeMillis();

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
                        if (document.getId().contains("_text_")) {
                        Lyrics lyric = document.toObject(Lyrics.class);
                        Log.d("ProjectActivity", document.toString());
                        Log.d("ProjectActivity", lyric.toString());

                        lyricsCards.add(lyric);
                        }
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
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permissions required to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
//        if (isRecording) {
//            Toast.makeText(this, "Stopping...", Toast.LENGTH_SHORT).show();
//            return;
//        }
        filePath = getExternalFilesDir(null).getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".mp3";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                Log.d("Recording", "Recording stopped, file saved at: " + filePath);
                Toast.makeText(this, "Recording stopped, file saved at: " + filePath, Toast.LENGTH_SHORT).show();
                uploadFileToFirestore(filePath);  // Trigger file upload
            } catch (RuntimeException e) {
                Log.e("Recording", "Stop recording failed", e);
                new File(filePath).delete();
            } finally {
                mediaRecorder = null;
            }
        }
    }


    //TODO
    private void uploadFileToFirestore(String filePath) {
        Uri fileUri = Uri.fromFile(new File(filePath));
        StorageReference storageRef = storage.getReference().child("audio/" + fileUri.getLastPathSegment());

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL and save metadata to Firestore
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveFileMetadataToFirestore(downloadUrl);
                    });
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Upload failed", e);
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveFileMetadataToFirestore(String downloadUrl) {
        Map<String, Object> audioData = new HashMap<>();
        audioData.put("projectID", projectID);
        audioData.put("hashtag","#custom_instrument");
        audioData.put("url", downloadUrl);
        audioData.put("timestamp", System.currentTimeMillis());

        String documentID = projectID + "_audio_" + System.currentTimeMillis();
        db.collection("project_component")
                .document(documentID)  // Use custom ID
                .set(audioData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding document with custom ID", e);
                });
    }


    private void loadAudioFiles() {
        db.collection("project_component")
                .whereEqualTo("projectID", projectID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    registrationCards.clear(); // Clear the list to avoid duplication
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (document.getId().contains("_audio_")) {
                            try {
                                // Extract fields from the Firestore document
                                String url = document.getString("url");
                                String hashtag = document.getString("hashtag");
                                String projectID = document.getString("projectID");
                                Long timestamp = document.getLong("timestamp");

                                // Validate retrieved fields
                                if (url == null || url.isEmpty()) {
                                    Log.e("ProjectActivity", "Missing or empty URL in document: " + document.getId());
                                    continue;
                                }
                                if (projectID == null) {
                                    Log.e("ProjectActivity", "Missing projectID in document: " + document.getId());
                                    continue;
                                }
                                if (timestamp == null) timestamp = 0L; // Default to 0 if missing

                                // Create and add the AudioFile object
                                AudioFile audioFile = new AudioFile(url, hashtag, projectID, timestamp);
                                registrationCards.add(audioFile);
                                Log.d("ProjectActivity", "AudioFile loaded: " + audioFile.toString());
                            } catch (Exception e) {
                                Log.e("ProjectActivity", "Error processing document: " + document.getId(), e);
                            }
                        }
                    }

                    // Notify adapter of data changes
                    if (recordAdapter != null) {
                        recordAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("ProjectActivity", "recordAdapter is null. Cannot update RecyclerView.");
                    }
                })
                .addOnFailureListener(exception -> {
                    Log.e("Firestore", "Error fetching documents", exception);
                    Toast.makeText(this, "Failed to load audio files", Toast.LENGTH_SHORT).show();
                });
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
