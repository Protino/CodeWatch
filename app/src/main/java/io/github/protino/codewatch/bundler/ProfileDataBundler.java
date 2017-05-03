package io.github.protino.codewatch.bundler;

import android.os.Bundle;

import icepick.Bundler;
import io.github.protino.codewatch.model.user.ProfileData;

/**
 * @author Gurupad Mamadapur
 */

public class ProfileDataBundler implements Bundler<ProfileData> {
    @Override
    public void put(String s, ProfileData profileData, Bundle bundle) {
        bundle.putSerializable(s, profileData);
    }

    @Override
    public ProfileData get(String s, Bundle bundle) {
        return (ProfileData) bundle.getSerializable(s);
    }
}
