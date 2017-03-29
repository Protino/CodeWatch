package io.github.protino.codewatch.utils;

import io.github.protino.codewatch.data.LeaderContract;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public final class Constants {

    /* Oauth related constants */
    public static final String REDIRECT_URI = "codewatch://callback";
    public static final String PREF_ACCESS_CODE = "pref_access_code";
    public static final String PREF_CODE_STATE = "pref_state";
    public static final String PREF_ACCESS_TOKEN = "pref_access_token";
    public static final String SCHEME = "codewatch";
    /* Stats range */
    public static final String _7_DAYS = "last_7_days";
    public static final String _30_DAYS = "last_30_days";
    public static final String _6_MONTHS = "last_6_months";
    public static final String _YEAR = "last_year";

    /* Goal types */
    public static final int LANGUAGE_GOAL = 0;
    public static final int PROJECT_DEADLINE_GOAL = 1;
    public static final int PROJECT_DAILY_GOAL = 2;

    /* API core constants */
    public static final String API_SUFFIX = "api/v1/users/current/";
    /* Cache constants */
    public static final String PREF_WAKATIME_DATA_UPDATED = "WAKATIME_DATA_PREF_KEY";
    public static final String PREF_FIREBASE_USER_DATA = "firebase_user_data_pref_key";
    public static final String PREF_FIREBASE_USER_ID = "firebase_user_id_pref_key";
    public static final String PREF_LEADERBOARD_UPDATED = "leaderboard_pref_key";
    public static final String PREF_USER_LEARNED_DRAWER = "pref_user_learned_drawer";

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

}
