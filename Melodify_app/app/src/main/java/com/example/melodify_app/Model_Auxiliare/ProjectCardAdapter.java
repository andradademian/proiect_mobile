package com.example.melodify_app.Model_Auxiliare;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.R;

import java.util.List;

public class ProjectCardAdapter extends RecyclerView.Adapter<ProjectCardAdapter.CardViewHolder> {

    private List<ProjectCard> dataList;

    public ProjectCardAdapter(List<ProjectCard> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ProjectCard cardData = dataList.get(position);
        holder.titleText.setText(cardData.getTitle());
        holder.descriptionText.setText(cardData.getDescription());

        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to navigate to the Project activity
            Toast.makeText(v.getContext(),"kbfjacbfa",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(v.getContext(), Project.class);

            // Optionally pass data if needed
            intent.putExtra("title", cardData.getTitle());
            intent.putExtra("description", cardData.getDescription());

            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText;

        CardViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title_text);
            descriptionText = itemView.findViewById(R.id.description_text);
        }
    }
}