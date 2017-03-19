package io.github.protino.codewatch.utils;

import io.github.protino.codewatch.data.LeaderContract;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public final class Constants {

    /* Oauth related constants */
    public static final String REDIRECT_URI = "codewatch://callback";
    public static final String ACCESS_CODE_PREF_KEY = "ACCESS_CODE_PREF_KEY";
    public static final String STATE_PREF_KEY = "STATE_PREF_KEY";
    public static final String ACCESS_TOKEN_PREF_KEY = "ACCESS_TOKEN_PREF_KEY";
    public static final String SCHEME = "codewatch";
    /* Stats range */
    public static final String _7_DAYS = "last_7_days";
    public static final String _30_DAYS = "last_30_days";
    public static final String _6_MONTHS = "last_6_months";
    public static final String _YEAR = "last_year";
    /* API core constants */
    public static final String API_SUFFIX = "api/v1/users/current/";
    /* Cache constants */
    public static final String WAKATIME_DATA_UPDATED = "WAKATIME_DATA_PREF_KEY";
    public static final String FIREBASE_USER_DATA_PREF_KEY = "firebase_user_data_pref_key";
    public static final String FIREBASE_USER_ID_PREF_KEY = "firebase_user_id_pref_key";
    public static final String LEADERBOARD_UPDATED = "leaderboard_pref_key";
    /*Projections*/
    public static final int COL_LEADER_ID = 0;
    public static final int COL_USER_ID = 1;
    public static final int COL_FULL_NAME = 2;
    public static final int COL_TOTAL_SECONDS = 3;
    public static final int COL_LANGUAGE_STATS = 4;
    public static final String[] LEADER_COLUMNS = {
            LeaderContract.LeaderEntry._ID,
            LeaderContract.LeaderEntry.COLUMN_USER_ID,
            LeaderContract.LeaderEntry.COLUMN_USER_NAME,
            LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS,
            LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS
    };
    public static final String LEADER_SORT_ORDER = LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS + " DESC";
    /* Firebase Constants*/
    public static final String WAKATIME_DATA_SYNC_JOB_TAG = "daily_sync_job";
    public static final int SYNC_TOLERANCE = 60;
    public static final int SYNC_PERIOD = 60 * 60 * 24;
    public static final String PERIODIC_SYNC_SCHEDULED_KEY = "periodic_sync_scheduled_key";

    private Constants() {
    }
}
