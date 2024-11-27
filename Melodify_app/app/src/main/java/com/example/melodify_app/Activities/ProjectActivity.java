package com.example.melodify_app.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.example.melodify_app.Model_Auxiliare.Lyrics;
import com.example.melodify_app.Model_Auxiliare.LyricsAdapter;
import com.example.melodify_app.Model_Auxiliare.ProjectCard;
import com.example.melodify_app.Model_Auxiliare.SpaceItemDecoration;
import com.example.melodify_app.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends Activity {

    private Button stopRecordingButton;
    List<Lyrics> cardDataList3;
    private Button addRecordingButton;
    private Button addLyricsButton;
    private ImageButton playRecordingButton;
    private ImageButton pinButton;

    private ImageButton backButton;

    private MediaRecorder mediaRecorder;
    private String filePath;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_layout);

//        ProjectCard project = (ProjectCard) getIntent().getSerializableExtra("CARD");
//        TextView songtitle = findViewById(R.id.textView3);
//        songtitle.setText(project.getTitle());
//
//        TextView songdescripptioin = findViewById(R.id.song_description);
//        songdescripptioin.setText(project.getDescription());


////        List<AudioFile> cardDataList2 = new ArrayList<>();
//        cardDataList2.add(new AudioFile("Title 1"));
//        cardDataList2.add(new AudioFile("Title 2"));
//        cardDataList2.add(new AudioFile("Title 3"));
//        cardDataList2.add(new AudioFile("Title 4"));
//        cardDataList2.add(new AudioFile("Title 3"));
//        cardDataList2.add(new AudioFile("Title 4"));

        cardDataList3 = new ArrayList<>();
        cardDataList3.add(new Lyrics(""));
        cardDataList3.add(new Lyrics(""));
        cardDataList3.add(new Lyrics(""));
        cardDataList3.add(new Lyrics(""));
        cardDataList3.add(new Lyrics(""));
        cardDataList3.add(new Lyrics(""));

        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LyricsAdapter(cardDataList3));

        int spacing = 45; // 20dp
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacing));


//        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                // Apply spacing to each item
//                outRect.left = spaceInPixels;
//                outRect.right = spaceInPixels;
//                outRect.top = spaceInPixels;
//
//                // Avoid adding extra space at the bottom of the last item
//                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
//                    outRect.bottom = spaceInPixels;
//                }
//            }
//        });
//
//        Log.d("Spacing", "Spacing in pixels: " + getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing));

        stopRecordingButton = findViewById(R.id.button6);
        addRecordingButton = findViewById(R.id.button5);
        addLyricsButton = findViewById(R.id.button7);
        backButton = findViewById(R.id.imageButton1);


        //playRecordingButton = findViewById(R.id.imageButton2);
        //pinButton = findViewById(R.id.imageButton7);

        filePath = getExternalCacheDir().getAbsolutePath() + "/recording.3gp"; // Adjust path as needed


        addRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startRecording(); // Start recording if permissions are granted
                } else {
                    requestPermissions(); // Request necessary permissions
                }
            }
        });

        // Set up Stop Recording Button functionality
        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(); // Stop the recording
            }
        });

        // Set up Play Recording Button functionality
//        playRecordingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playRecording(); // Play the recording
//            }
//        });

        // Set up Delete Song Button functionality
        addLyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecording(); // Delete the recorded file
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // Optional: To prevent stacking activities
        });
    }

    // Check if the required permissions are granted
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Request audio recording and storage permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permissions are required to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Start the audio recording
    private void startRecording() {
        if (isRecording) {
            Toast.makeText(this, "Recording is already in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Set the audio source (microphone)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Output format
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // Audio encoder
            mediaRecorder.setOutputFile(filePath); // Path to save the recording

            mediaRecorder.prepare(); // Prepare the recorder
            mediaRecorder.start(); // Start recording
            isRecording = true;

            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            Log.i("Recording Status", "Recording started successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
            Log.e("Recording Error", "Error while preparing or starting recording: " + e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording setup error", Toast.LENGTH_SHORT).show();
            Log.e("Recording Error", "IllegalStateException: " + e.getMessage());
        }
    }

    // Stop the audio recording
    private void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                Log.i("Recording Status", "Recording stopped successfully");
            } catch (RuntimeException e) {
                Log.e("Recording Error", "Error while stopping recording: " + e.getMessage());
                Toast.makeText(this, "Failed to stop recording", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No recording is in progress", Toast.LENGTH_SHORT).show();
        }
    }

    // Play the recorded audio
    private void playRecording() {
        // Placeholder for playing the recorded audio using MediaPlayer
        Toast.makeText(this, "Playing recording", Toast.LENGTH_SHORT).show();
    }

    // Delete the recorded file
    private void deleteRecording() {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Release resources when done with recording
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release(); // Release the media recorder
            mediaRecorder = null;
        }
    }
}
