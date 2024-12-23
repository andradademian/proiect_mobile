package com.example.melodify_app.Model_Auxiliare;

public class Lyrics {
    private String text;
    private String projectId;

    public Lyrics() {
        // Required empty constructor for Firestore
    }

    public Lyrics(String text, String projectId) {
        this.text = text;
        this.projectId = projectId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
