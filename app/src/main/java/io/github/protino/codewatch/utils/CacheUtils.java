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

package io.github.protino.codewatch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.Days;

import io.github.protino.codewatch.model.AccessToken;

import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_CODE;
import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_TOKEN;
import static io.github.protino.codewatch.utils.Constants.PREF_APP_LAST_USAGE;
import static io.github.protino.codewatch.utils.Constants.PREF_BASIC_USER_DETAILS;
import static io.github.protino.codewatch.utils.Constants.PREF_CODE_STATE;
import static io.github.protino.codewatch.utils.Constants.PREF_FIREBASE_SETUP;
import static io.github.protino.codewatch.utils.Constants.PREF_FIREBASE_USER_ID;
import static io.github.protino.codewatch.utils.Constants.PREF_START_OF_CONSECUTIVE_DAYS;
import static io.github.protino.codewatch.utils.Constants.PREF_WAKATIME_USER_ID;

/**
 * Helper class to fetch data from {@link SharedPreferences}
 *
 * @author Gurupad Mamadapur
 */

public class CacheUtils {

    /**
     * Delete all the login info
     *
     * @param context needed to fetch defaultSharedPreferences
     */
    public static void clearLoginInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_ACCESS_CODE);
        editor.remove(PREF_ACCESS_TOKEN);
        editor.remove(PREF_CODE_STATE);
        editor.remove(PREF_BASIC_USER_DETAILS);
        editor.remove(PREF_FIREBASE_USER_ID);
        editor.remove(PREF_FIREBASE_SETUP);
        editor.apply();
    }

    /**
     * Checks whether the users is logged in or not
     */
    public static boolean isLoggedIn(Context context) {
        AccessToken accessToken = getAccessToken(context);
        if (accessToken == null) {
            return false;
        }
        long currentTime = (long) (System.currentTimeMillis() / 1e3);
        long expiryTime = accessToken.getRetrievalTime() + accessToken.getExpiresIn() - AccessToken.REFRESH_THRESHOLD;
        return currentTime < expiryTime;
    }

    /**
     * @param context needed to fetch defaultSharedPreferences
     * @return access token
     */
    public static AccessToken getAccessToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accessTokenJson = sharedPreferences.getString(PREF_ACCESS_TOKEN, null);
        if (accessTokenJson == null) {
            return null;
        }
        return new Gson().fromJson(accessTokenJson, AccessToken.class);
    }

    /**
     * @param context needed to fetch defaultSharedPreferences
     * @return true if firebase account is setup and data fields are initialized
     */
    public static boolean isFireBaseSetup(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uid = sharedPreferences.getString(PREF_FIREBASE_USER_ID, null);
        boolean updated = sharedPreferences.getBoolean(PREF_FIREBASE_SETUP, false);

        return uid != null && updated;
    }

    /**
     * @param context needed to fetch defaultSharedPreferences
     * @return firebase userId
     */
    public static String getFirebaseUserId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FIREBASE_USER_ID, null);
    }

    /**
     * @param context needed to fetch defaultSharedPreferences
     * @return wakatime userId
     */
    public static String getWakatimeUserId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_WAKATIME_USER_ID, null);
    }

    /**
     * Updates the number of the days the app was used
     *
     * @param context needed to fetch defaultSharedPreferences
     */
    public static void updateAppUsage(Context context) {

        long currentTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long lastUsageTime = sharedPreferences.getLong(PREF_APP_LAST_USAGE, -1);

        if (lastUsageTime == -1) {
            editor.putLong(PREF_APP_LAST_USAGE, currentTime);
            editor.putLong(PREF_START_OF_CONSECUTIVE_DAYS, currentTime);
        } else {
            //First check if it's been more than a day since last usage
            int days = Days.daysBetween(new DateTime(lastUsageTime), new DateTime(currentTime)).getDays();
            if (days > 1) {
                editor.putLong(PREF_START_OF_CONSECUTIVE_DAYS, currentTime);
            }
            editor.putLong(PREF_APP_LAST_USAGE, currentTime);
        }
        editor.apply();
    }

    /**
     * @param context needed to fetch defaultSharedPreferences
     * @return number of days the app was opened consecutively
     */
    public static int getConsecutiveDays(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long lastConsecutiveUsageTime = sharedPreferences.getLong(PREF_START_OF_CONSECUTIVE_DAYS, -1);
        return lastConsecutiveUsageTime == -1
                ? 0
                : Days.daysBetween(new DateTime(lastConsecutiveUsageTime), new DateTime(System.currentTimeMillis())).getDays();
    }
}
