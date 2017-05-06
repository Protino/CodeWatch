/*
 * Copyright 2017 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.codewatch.bundler;

import android.os.Bundle;

import icepick.Bundler;
import io.github.protino.codewatch.model.user.ProfileData;

/**
 * Custom bundler to preserve {@link ProfileData} object
 *
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
