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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Provider class for Leaderboard database
 */
public class LeaderProvider extends ContentProvider {

    protected static final int LEADER = 100;
    protected static final int PROFILE = 101;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private static final SQLiteQueryBuilder profileQueryBuilder;

    //leader.user_id = ?
    private static final String userIdSelection =
            LeaderContract.LeaderEntry.TABLE_NAME +
                    "." + LeaderContract.LeaderEntry.COLUMN_USER_ID + " = ? ";

    static {
        profileQueryBuilder = new SQLiteQueryBuilder();

        profileQueryBuilder.setTables(LeaderContract.LeaderEntry.TABLE_NAME);
    }

    private LeaderDbHelper leaderDbHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(LeaderContract.CONTENT_AUTHORITY, "leader", LEADER);
        uriMatcher.addURI(LeaderContract.CONTENT_AUTHORITY, "leader/*", PROFILE);
        return uriMatcher;
    }

    //Lifecycle start
    @Override
    public boolean onCreate() {
        leaderDbHelper = new LeaderDbHelper(getContext());
        return true;
    }
    //Lifecycle end


    private Cursor getProfileByUserId(Uri uri, String[] projection, String sortOrder) {
        String userId = LeaderContract.LeaderEntry.getUserIdFromUri(uri);

        return profileQueryBuilder.query(leaderDbHelper.getReadableDatabase(),
                projection,
                userIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case PROFILE:
                return LeaderContract.LeaderEntry.CONTENT_ITEM_TYPE;
            case LEADER:
                return LeaderContract.LeaderEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {
            // "leader/*"
            case PROFILE: {
                retCursor = getProfileByUserId(uri, projection, sortOrder);
                break;
            }
            // "leader"
            case LEADER: {
                retCursor = leaderDbHelper.getReadableDatabase().query(
                        LeaderContract.LeaderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = leaderDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case LEADER: {
                long _id = db.insert(LeaderContract.LeaderEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = LeaderContract.LeaderEntry.buildLeaderUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = leaderDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;
        switch (match) {
            case LEADER: {
                rowsDeleted = sqLiteDatabase.delete(LeaderContract.LeaderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = leaderDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;
        switch (match) {
            case LEADER: {
                rowsUpdated = sqLiteDatabase.update(
                        LeaderContract.LeaderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = leaderDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case LEADER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(LeaderContract.LeaderEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // Required for unit testing only
    @Override
    public void shutdown() {
        leaderDbHelper.close();
        super.shutdown();
    }
}