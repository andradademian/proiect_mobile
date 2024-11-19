package com.example.melodify_app.Model_Auxiliare;

public class AudioFile {
    public AudioFile(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    String file_name;
    String hashtag; //instrument
}
