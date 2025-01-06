package com.example.melodify_app.Model_Auxiliare;

public class AudioFile {
    String url;
    String hashtag; // instrument
    String projectID;
    long timestamp;

    // Default constructor (required for Firestore deserialization)
    public AudioFile() {
    }

    // Parameterized constructor
    public AudioFile(String file_name, String hashtag, String projectID, long timestamp) {
        this.url = file_name != null ? file_name : "";  // Fallback to an empty string
        this.hashtag = hashtag != null ? hashtag : "#unknown";  // Fallback to default
        this.projectID = projectID != null ? projectID : "unknown";
        this.timestamp = timestamp > 0 ? timestamp : System.currentTimeMillis();
    }


    // Getters and setters
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

    @Override
    public String toString() {
        return "AudioFile{" +
                "url='" + url + '\'' +
                ", hashtag='" + hashtag + '\'' +
                ", projectID='" + projectID + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
