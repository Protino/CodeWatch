package io.github.protino.codewatch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import io.github.protino.codewatch.model.AccessToken;

import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_CODE;
import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_TOKEN;
import static io.github.protino.codewatch.utils.Constants.PREF_CODE_STATE;
import static io.github.protino.codewatch.utils.Constants.PREF_FIREBASE_SETUP;
import static io.github.protino.codewatch.utils.Constants.PREF_FIREBASE_USER_ID;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
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
        editor.apply();
    }

    /**
     * Checks whether the users is logged in or not
     *
     * @param context
     */
    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accessTokenJson = sharedPreferences.getString(PREF_ACCESS_TOKEN, null);
        if (accessTokenJson == null) {
            return false;
        }
        AccessToken accessToken = new Gson().fromJson(accessTokenJson, AccessToken.class);
        long currentTime = (long) (System.currentTimeMillis() / 1e3);
        long expiryTime = accessToken.getRetrievalTime() + accessToken.getExpiresIn() - AccessToken.REFRESH_THRESHOLD;
        return currentTime < expiryTime;
    }

    public static boolean isFireBaseSetup(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uid = sharedPreferences.getString(PREF_FIREBASE_USER_ID, null);
        boolean updated = sharedPreferences.getBoolean(PREF_FIREBASE_SETUP, false);

        return uid != null && updated;
    }

    public static String getFirebaseUserId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FIREBASE_USER_ID,null);
    }
}
