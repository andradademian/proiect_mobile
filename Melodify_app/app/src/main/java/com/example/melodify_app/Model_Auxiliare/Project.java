package com.example.melodify_app.Model_Auxiliare;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable {
    String projectID;
    private static int idCounter = 1;

    String userID;
    String name;
    String description;
    ArrayList<String> textFiles;
    ArrayList<AudioFile> audioFiles;

    public Project(String name, String description,String userID) {
        this.projectID=String.valueOf(idCounter++);
        this.name = name;
        this.description = description;
        this.userID=userID;
    }

    public Project(String projectID, String name, String description, String userID) {
        this.projectID = projectID;
        this.name = name;
        this.description = description;
        this.userID=userID;
    }
}
