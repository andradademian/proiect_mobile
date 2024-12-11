package com.example.melodify_app.Model_Auxiliare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.R;

import java.util.List;

public class RegistrationCardAdapter extends RecyclerView.Adapter<RegistrationCardAdapter.ViewHolder>{
    private final List<AudioFile> dataList;

    public RegistrationCardAdapter(List<AudioFile> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.registration_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to views in registration_card.xml (if applicable)
        AudioFile data = dataList.get(position);
        // For example:
        // holder.textView.setText(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define views in registration_card.xml
        public ImageButton playButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.imageButton);
        }
    }
}
