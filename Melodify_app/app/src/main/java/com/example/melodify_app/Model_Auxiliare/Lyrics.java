package com.example.melodify_app.Model_Auxiliare;

public class Lyrics {
    private String text;
    private String projectID;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public Lyrics() {
        // Required empty constructor for Firestore
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public Lyrics(String text, String projectID) {
        this.text = text;
        this.projectID = projectID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "text='" + text + '\'' +
                ", projectId='" + projectID + '\'' +
                ", index=" + index +
                '}';
    }
}
