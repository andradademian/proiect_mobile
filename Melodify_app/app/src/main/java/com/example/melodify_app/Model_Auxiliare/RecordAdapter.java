//package com.example.melodify_app.Model_Auxiliare;
//import android.content.Context;
//
//
//import android.media.MediaPlayer;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.melodify_app.R;
//
//import java.io.IOException;
//import java.util.List;
//public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
//    private List<AudioFile> audioFiles;
//    private Context context;
//
//    public RecordAdapter(List<AudioFile> audioFiles, Context context) {
//        this.audioFiles = audioFiles;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.registration_card, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        AudioFile audioFile = audioFiles.get(position);
//        holder.textView.setText(audioFile.getHashtag());
//        // Example: Implement media player for audio preview
//        holder.itemView.setOnClickListener(v -> {
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(audioFile.getUrl());
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return audioFiles.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(R.id.cardRegistration);
//        }
//    }
//}
