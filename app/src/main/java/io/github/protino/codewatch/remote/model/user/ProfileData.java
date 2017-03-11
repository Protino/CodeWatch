
package io.github.protino.codewatch.remote.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("email_public")
    @Expose
    private Boolean emailPublic;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("has_premium_features")
    @Expose
    private Boolean hasPremiumFeatures;
    @SerializedName("human_readable_website")
    @Expose
    private String humanReadableWebsite;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("is_email_confirmed")
    @Expose
    private String isEmailConfirmed;
    @SerializedName("is_hireable")
    @Expose
    private Boolean isHireable;
    @SerializedName("languages_used_public")
    @Expose
    private Boolean languagesUsedPublic;
    @SerializedName("last_heartbeat")
    @Expose
    private String lastHeartbeat;
    @SerializedName("last_plugin")
    @Expose
    private String lastPlugin;
    @SerializedName("last_plugin_name")
    @Expose
    private String lastPluginName;
    @SerializedName("last_project")
    @Expose
    private String lastProject;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("logged_time_public")
    @Expose
    private Boolean loggedTimePublic;
    @SerializedName("modified_at")
    @Expose
    private Object modifiedAt;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("photo_public")
    @Expose
    private Boolean photoPublic;
    @SerializedName("plan")
    @Expose
    private String plan;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("website")
    @Expose
    private String website;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailPublic() {
        return emailPublic;
    }

    public void setEmailPublic(Boolean emailPublic) {
        this.emailPublic = emailPublic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getHasPremiumFeatures() {
        return hasPremiumFeatures;
    }

    public void setHasPremiumFeatures(Boolean hasPremiumFeatures) {
        this.hasPremiumFeatures = hasPremiumFeatures;
    }

    public String getHumanReadableWebsite() {
        return humanReadableWebsite;
    }

    public void setHumanReadableWebsite(String humanReadableWebsite) {
        this.humanReadableWebsite = humanReadableWebsite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsEmailConfirmed() {
        return isEmailConfirmed;
    }

    public void setIsEmailConfirmed(String isEmailConfirmed) {
        this.isEmailConfirmed = isEmailConfirmed;
    }

    public Boolean getIsHireable() {
        return isHireable;
    }

    public void setIsHireable(Boolean isHireable) {
        this.isHireable = isHireable;
    }

    public Boolean getLanguagesUsedPublic() {
        return languagesUsedPublic;
    }

    public void setLanguagesUsedPublic(Boolean languagesUsedPublic) {
        this.languagesUsedPublic = languagesUsedPublic;
    }

    public String getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(String lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getLastPlugin() {
        return lastPlugin;
    }

    public void setLastPlugin(String lastPlugin) {
        this.lastPlugin = lastPlugin;
    }

    public String getLastPluginName() {
        return lastPluginName;
    }

    public void setLastPluginName(String lastPluginName) {
        this.lastPluginName = lastPluginName;
    }

    public String getLastProject() {
        return lastProject;
    }

    public void setLastProject(String lastProject) {
        this.lastProject = lastProject;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getLoggedTimePublic() {
        return loggedTimePublic;
    }

    public void setLoggedTimePublic(Boolean loggedTimePublic) {
        this.loggedTimePublic = loggedTimePublic;
    }

    public Object getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Object modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Boolean getPhotoPublic() {
        return photoPublic;
    }

    public void setPhotoPublic(Boolean photoPublic) {
        this.photoPublic = photoPublic;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
