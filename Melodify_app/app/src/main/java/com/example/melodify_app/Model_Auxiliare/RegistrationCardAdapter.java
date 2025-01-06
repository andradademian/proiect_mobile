package com.example.melodify_app.Model_Auxiliare;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.R;

import java.io.IOException;
import java.util.List;

public class RegistrationCardAdapter extends RecyclerView.Adapter<RegistrationCardAdapter.ViewHolder> {

    private final List<AudioFile> dataList; // List of AudioFile objects
    private Context context;

    public RegistrationCardAdapter(List<AudioFile> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.registration_card, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioFile audioFile = dataList.get(position);

        // Display metadata in the TextView(s)
        holder.hashtagTextView.setText(audioFile.getHashtag());
        holder.timestampTextView.setText(formatTimestamp(audioFile.getTimestamp()));

        // Set up play button to play audio
        holder.playButton.setOnClickListener(v -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFile.getUrl()); // Audio file URL
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(context, "Playing audio...", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, "Failed to play audio", Toast.LENGTH_SHORT).show();
                Log.e("RegistrationCardAdapter", "Error playing audio", e);
            }
        });

        // Handle long press to delete the item
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dataList.remove(position); // Remove item from the list
                        notifyItemRemoved(position); // Notify adapter
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton playButton;
        public TextView hashtagTextView;
        public TextView timestampTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.imageButton); // Match the ID in registration_card.xml
           // hashtagTextView = itemView.findViewById(R.id.hashtagTextView); // ID for hashtag TextView
           // timestampTextView = itemView.findViewById(R.id.timestampTextView); // ID for timestamp TextView
        }
    }

    // Helper method to format timestamp
    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }
}
