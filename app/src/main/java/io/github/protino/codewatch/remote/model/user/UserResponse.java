
package io.github.protino.codewatch.remote.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("data")
    @Expose
    private ProfileData profileData;

    public ProfileData getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
    }

}
