package com.example.melodify_app.Model_Auxiliare;

import java.io.Serializable;

public class ProjectCard implements Serializable {
    private String id;
    private String title;
    private String description;

    public ProjectCard(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ProjectCard(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}

