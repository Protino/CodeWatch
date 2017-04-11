package io.github.protino.codewatch.utils;

import android.util.SparseIntArray;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.data.LeaderContract;

/**
 * @author Gurupad Mamadapur
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

    /* Badge types */
    public static final int GOLD_BADGE = 0;
    public static final int SILVER_BADGE = 1;
    public static final int BRONZE_BADGE = 2;

    /* Goal types */
    public static final int LANGUAGE_GOAL = 0;
    public static final int PROJECT_DEADLINE_GOAL = 1;
    public static final int PROJECT_DAILY_GOAL = 2;

    /* API core constants */
    public static final String API_SUFFIX = "api/v1/users/current/";
    public static final String WAKATIME_BASE_URL = "https://wakatime.com/";

    /* Cache constants */
    public static final String PREF_WAKATIME_DATA_UPDATED = "WAKATIME_DATA_PREF_KEY";
    public static final String PREF_FIREBASE_USER_DATA = "firebase_user_data_pref_key";
    public static final String PREF_FIREBASE_USER_ID = "firebase_user_id_pref_key";
    public static final String PREF_FIREBASE_SETUP = "firebase_setup_key";
    public static final String PREF_LEADERBOARD_UPDATED = "leaderboard_pref_key";
    public static final String PREF_BASIC_USER_DETAILS = "pref_basic_user_detail_key";
    public static final String PREF_USER_LEARNED_DRAWER = "pref_user_learned_drawer";

    /*Projections */
    public static final int COL_LEADER_ID = 0;
    public static final int COL_USER_ID = 1;
    public static final int COL_DISPLAY_NAME = 2;
    public static final int COL_TOTAL_SECONDS = 3;
    public static final int COL_LANGUAGE_STATS = 4;
    public static final int COL_PHOTO_URL = 5;
    public static final int COL_DAILY_AVERAGE = 6;

    public static final String[] LEADER_COLUMNS = {
            LeaderContract.LeaderEntry._ID,
            LeaderContract.LeaderEntry.COLUMN_USER_ID,
            LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME,
            LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS,
            LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS,
            LeaderContract.LeaderEntry.COLUMN_PHOTO,
            LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE
    };
    public static final String LEADER_SORT_ORDER = LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS + " DESC";
    /* Firebase Constants */
    public static final String WAKATIME_DATA_SYNC_JOB_TAG = "daily_sync_job";
    public static final int SYNC_TOLERANCE = 60;
    public static final int SYNC_PERIOD = 60 * 60 * 24;
    public static final String PERIODIC_SYNC_SCHEDULED_KEY = "periodic_sync_scheduled_key";

    /* Goal Detail keys */
    public static final String GOAL_DATA_KEY = "goal_data_key";
    public static final String GOAL_TYPE_KEY = "goal_type_key";


    /**
     * ==========================
     * Achievements Bit positions
     * ==========================
     * <pre>
     *  20 bits for each badge type
     *  0-19  -> gold
     *  20-39 -> silver
     *  40-59 -> bronze
     * </pre>
     * These are dependent on badges declared in {@link io.github.protino.codewatch.R.array}
     */

    //Gold badges
    public static final int DEVOTED = 0;
    public static final int INSOMNIAC = 1;
    public static final int LEADER = 2;

    //silver badges
    public static final int ARDENT = 20;
    public static final int HARD_WORKING = 21;
    public static final int MASTER = 22;

    //bronze badges
    public static final int LOYAL = 40;
    public static final int SANE = 41;
    public static final int COMPETITOR = 42;
    public static final int CURIOUS = 43;
    public static final int FOCUSED = 44;

    public static final SparseIntArray ACHIEVEMENTS_MAP;

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(R.array.devoted, DEVOTED);
        sparseIntArray.put(R.array.insomniac, INSOMNIAC);
        sparseIntArray.put(R.array.leader, LEADER);

        sparseIntArray.put(R.array.ardent, ARDENT);
        sparseIntArray.put(R.array.hard_working, HARD_WORKING);
        sparseIntArray.put(R.array.master, MASTER);

        sparseIntArray.put(R.array.loyal, LOYAL);
        sparseIntArray.put(R.array.sane, SANE);
        sparseIntArray.put(R.array.competitor, COMPETITOR);
        sparseIntArray.put(R.array.curious, CURIOUS);
        sparseIntArray.put(R.array.focused, FOCUSED);

        ACHIEVEMENTS_MAP = sparseIntArray;
    }
}
