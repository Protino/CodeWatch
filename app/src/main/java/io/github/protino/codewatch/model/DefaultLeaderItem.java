package io.github.protino.codewatch.model;

import java.util.Map;

/**
 * @author Gurupad Mamadapur
 */

public class DefaultLeaderItem {

    //user details
    private String userId;
    private String displayName;
    private String photoUrl;

    //stats
    private int dailyAverage;
    private int totalSeconds;
    private Map<String,Integer> languageStats;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, Integer> getLanguageStats() {
        return languageStats;
    }

    public void setLanguageStats(Map<String, Integer> languageStats) {
        this.languageStats = languageStats;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
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
}
