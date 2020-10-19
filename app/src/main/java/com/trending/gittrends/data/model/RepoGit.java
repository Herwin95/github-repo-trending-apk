package com.trending.gittrends.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class RepoGit {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int stars;
    private String language;
    private String languageColor;

    public RepoGit() {}

    @Ignore
    public RepoGit(String title, String description, int stars, String language, String languageColor) {
        this.title = title;
        this.description = description;
        this.stars = stars;
        this.language = language;
        this.languageColor = languageColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageColor() {
        return languageColor;
    }

    public void setLanguageColor(String languageColor) {
        this.languageColor = languageColor;
    }

}
