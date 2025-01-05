package com.example.melodify_app.Model_Auxiliare;

public class AudioFile {
    String url;
    String hashtag; //instrument
    String projectID;
    long timestamp;

    public AudioFile(String file_name, String hashtag, String projectID, long timestamp) {
        this.url = file_name;
        this.hashtag = hashtag;
        this.projectID = projectID;
        this.timestamp=timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
