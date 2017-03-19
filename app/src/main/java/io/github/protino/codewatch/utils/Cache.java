package io.github.protino.codewatch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import io.github.protino.codewatch.remote.model.AccessToken;

import static io.github.protino.codewatch.utils.Constants.ACCESS_CODE_PREF_KEY;
import static io.github.protino.codewatch.utils.Constants.ACCESS_TOKEN_PREF_KEY;
import static io.github.protino.codewatch.utils.Constants.STATE_PREF_KEY;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class Cache {

    /**
     * Delete all the login info
     *
     * @param context needed to fetch defaultSharedPreferences
     */
    public static void clearLoginInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_CODE_PREF_KEY);
        editor.remove(ACCESS_TOKEN_PREF_KEY);
        editor.remove(STATE_PREF_KEY);
        editor.apply();
    }

    /**
     * Checks whether the users is logged in or not
     *
     * @param context
     */
    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accessTokenJson = sharedPreferences.getString(ACCESS_TOKEN_PREF_KEY, null);
        if (accessTokenJson == null) {
            return false;
        }
        AccessToken accessToken = new Gson().fromJson(accessTokenJson, AccessToken.class);
        long currentTime = (long) (System.currentTimeMillis() / 1e3);
        long expiryTime = accessToken.getRetrievalTime() + accessToken.getExpiresIn() - AccessToken.REFRESH_THRESHOLD;
        return currentTime < expiryTime;
    }
}
