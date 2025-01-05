package com.example.melodify_app.Model_Auxiliare;

public class AudioFile {
    String file_name;
    String hashtag; //instrument
    String projectID;

    public AudioFile(String file_name, String hashtag, String projectID) {
        this.file_name = file_name;
        this.hashtag = hashtag;
        this.projectID = projectID;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }


}
