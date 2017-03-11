
package io.github.protino.codewatch.remote.model.leaders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("email_public")
    @Expose
    private Boolean emailPublic;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("human_readable_website")
    @Expose
    private String humanReadableWebsite;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("photo_public")
    @Expose
    private Boolean photoPublic;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("website")
    @Expose
    private String website;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
