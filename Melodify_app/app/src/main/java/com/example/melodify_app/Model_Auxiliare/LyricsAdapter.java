package com.example.melodify_app.Model_Auxiliare;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodify_app.R;

import java.util.List;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.CardViewHolder> {

    private List<Lyrics> dataList;

    public LyricsAdapter(List<Lyrics> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyrics_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Lyrics cardData = dataList.get(position);
        holder.input.setText(cardData.getInput()); // Display the initial lyrics

        // Adding a TextWatcher to update the Lyrics object when the text changes
        holder.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Optional: Handle logic before text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Update the Lyrics object with the new text
                cardData.setInput(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Optional: Handle logic after text is changed
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        EditText input;

        CardViewHolder(View itemView) {
            super(itemView);
            input = itemView.findViewById(R.id.editTextLyrics); // Your EditText for lyrics input
        }
    }
}
