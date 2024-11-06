package com.example.melodify_app;

public class ProjectCard {
    private String title;
    private String description;

    public ProjectCard(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

