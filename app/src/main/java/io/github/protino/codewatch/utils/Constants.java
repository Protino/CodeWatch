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

import android.support.annotation.IntDef;
import android.util.SparseIntArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.data.LeaderContract;

/**
 * These constant are not subject to localization. Hence it is much better
 * to store them here instead of string.xml which requires a context to fetch
 *
 * @author Gurupad Mamadapur
 */

public final class Constants {

    /* Oauth related constants */
    public static final String PREF_ACCESS_CODE = "pref_access_code";
    public static final String PREF_CODE_STATE = "pref_state";
    public static final String PREF_ACCESS_TOKEN = "pref_access_token";

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
    public static final String PREF_FIREBASE_USER_DATA = "firebase_user_data_pref_key";
    public static final String PREF_FIREBASE_USER_ID = "firebase_user_id_pref_key";
    public static final String PREF_WAKATIME_USER_ID = "wakatime_user_pref_key";
    public static final String PREF_FIREBASE_SETUP = "firebase_setup_key";
    public static final String PREF_BASIC_USER_DETAILS = "pref_basic_user_detail_key";
    public static final String PREF_USER_LEARNED_DRAWER = "pref_user_learned_drawer";
    public static final String PREF_APP_LAST_USAGE = "pref_app_last_usage_key";
    public static final String PREF_START_OF_CONSECUTIVE_DAYS = "pref_start_of_consec_days";

    /* Error Codes */
    public static final int NONE = -1;
    public static final int STATS_UPDATING = 0;
    public static final int INTERNET_OFF = 1;
    public static final int SERVER_DOWN = 2;
    public static final int EMAIL_UNCONFIRMED = 3;
    public static final int UNKNOWN_ERROR = 4;

    /*Projections */
    public static final int COL_LEADER_ID = 0;
    public static final int COL_USER_ID = 1;
    public static final int COL_DISPLAY_NAME = 2;
    public static final int COL_TOTAL_SECONDS = 3;
    public static final int COL_LANGUAGE_STATS = 4;
    public static final int COL_PHOTO_URL = 5;
    public static final int COL_DAILY_AVERAGE = 6;
    public static final int COL_RANK = 7;

    public static final String[] LEADER_COLUMNS = {
            LeaderContract.LeaderEntry._ID,
            LeaderContract.LeaderEntry.COLUMN_USER_ID,
            LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME,
            LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS,
            LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS,
            LeaderContract.LeaderEntry.COLUMN_PHOTO,
            LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE,
            LeaderContract.LeaderEntry.COLUMN_RANK
    };
    public static final String LEADER_SORT_ORDER = LeaderContract.LeaderEntry.COLUMN_RANK + " ASC";

    /* Firebase Constants */
    public static final String WAKATIME_DATA_SYNC_JOB_TAG = "daily_sync_job";
    public static final int SYNC_PERIOD = 60 * 60 * 24;
    public static final int SYNC_TOLERANCE = SYNC_PERIOD + 60;
    public static final String PERIODIC_SYNC_SCHEDULE_KEY = "periodic_sync_scheduled_key";
    public static final String APP_UPDATE_KEY = "force_app_update";
    public static final String ACTION_DATA_UPDATED = "action_data_updated";


    /* Notification Constants */
    public static final int GOAL_NOTIFICATION_ID = 1;
    public static final int LEADERBOARD_NOTIFICATION_ID = 3;

    //My wakatime ID
    public static final String DEVELOPER_ID = "5c93d61f-b71b-4406-8e34-f86755d5df18";


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

    public static final int[] GOLD_BADGES = new int[]{DEVOTED, INSOMNIAC, LEADER};
    public static final int[] SILVER_BADGES = new int[]{ARDENT, HARD_WORKING, MASTER};
    public static final int[] BRONZE_BADGES = new int[]{COMPETITOR, SANE, LOYAL, CURIOUS, FOCUSED};
    public static final String FA_CATEGORY_FRAGMENT = "category_fragment";
    public static final String FA_ITEM_ID = "fragment_item_id";


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

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, STATS_UPDATING, SERVER_DOWN, INTERNET_OFF, UNKNOWN_ERROR, EMAIL_UNCONFIRMED})
    public @interface ErrorCodes {
    }
}

