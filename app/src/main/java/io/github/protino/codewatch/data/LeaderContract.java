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
package io.github.protino.codewatch.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import io.github.protino.codewatch.BuildConfig;

/**
 * Defines table and column names for the weather database.
 */
public class LeaderContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".leaderProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LEADER = "leader";

    /* Inner class that defines the table contents of the leader table */
    public static final class LeaderEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LEADER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LEADER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LEADER;


        public static final String TABLE_NAME = "leader";

        // User id as returned by API, to identify the user
        public static final String COLUMN_USER_ID = "user_id";

        //unnecessary while displaying leaderboard, but required when displaying profile
        public static final String COLUMN_RANK = "rank";

        // User coding activity
        public static final String COLUMN_DAILY_AVERAGE = "daily_average";
        public static final String COLUMN_TOTAL_SECONDS = "total_seconds";
        public static final String COLUMN_LANGUAGE_STATS = "language_stats";

        // User details
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_WEBSITE = "website";

        public static Uri buildLeaderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildProfileUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }

        public static String getUserIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
