package io.github.protino.codewatch.model.firebase;

import java.util.Map;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class User {

    /* User basic profile data - unlikely to change often */
    private String userId;
    private String displayName;
    private String email;
    private String website;
    private String isEmailConfirmed;
    private String timeZone;
    private String location;
    private String photoUrl;

    /* Premium user of wakatime*/
    private String currentPlan;
    private Boolean hasPremiumFeatures;

    /* Statistics */
    private Stats stats;

    /* Projects */
    /*
    IMPORTANT! - For some reason wakatime doesn't actually respect the project id
    It very irrelevant in the API.
    */
    private Map<String, Project> projects;

    //Duplication for faster loading
    private Map<String,Integer> timeSpentOnProjects; // project name and time spent on it over a week

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIsEmailConfirmed() {
        return isEmailConfirmed;
    }

    public void setIsEmailConfirmed(String isEmailConfirmed) {
        this.isEmailConfirmed = isEmailConfirmed;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLocation() {
        return location;
    }

    public User setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public User setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public String getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(String currentPlan) {
        this.currentPlan = currentPlan;
    }

    public Boolean getHasPremiumFeatures() {
        return hasPremiumFeatures;
    }

    public void setHasPremiumFeatures(Boolean hasPremiumFeatures) {
        this.hasPremiumFeatures = hasPremiumFeatures;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Map<String, Project> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, Project> projects) {
        this.projects = projects;
    }

    public Map<String, Integer> getTimeSpentOnProjects() {
        return timeSpentOnProjects;
    }

    public void setTimeSpentOnProjects(Map<String, Integer> timeSpentOnProjects) {
        this.timeSpentOnProjects = timeSpentOnProjects;
    }
}