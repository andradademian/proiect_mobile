package com.example.melodify_app.Model_Auxiliare;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.Activities.ProjectActivity;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ProjectCard cardData = dataList.get(position);
        holder.titleText.setText(cardData.getTitle());
        holder.descriptionText.setText(cardData.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),"slay",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(v.getContext(), ProjectActivity.class);
            intent.putExtra("CARD", cardData);
            intent.putExtra("USER",cardData.getUser());
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