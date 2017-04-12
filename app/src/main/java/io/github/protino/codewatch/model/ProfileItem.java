package io.github.protino.codewatch.model;

import java.util.List;

/**
 * @author Gurupad Mamadapur
 */
public class ProfileItem {

    private String name;
    private String photoUrl;
    private int dailyAverage;
    private int rank;

    private String website;
    private String email;
    private String location;
    private String languageStats;

    private List<Integer> unlockedAchievements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getDailyAverage() {
        return dailyAverage;
    }

    public void setDailyAverage(int dailyAverage) {
        this.dailyAverage = dailyAverage;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguageStats() {
        return languageStats;
    }

    public void setLanguageStats(String languageStats) {
        this.languageStats = languageStats;
    }

    public List<Integer> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(List<Integer> unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
