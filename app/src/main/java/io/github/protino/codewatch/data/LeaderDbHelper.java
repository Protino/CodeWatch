/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.protino.codewatch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for leader data.
 */
public class LeaderDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "leader.db";
    // Database version
    private static final int DATABASE_VERSION = 3;

    public LeaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Lifecycle start
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LEADER_TABLE = "CREATE TABLE " + LeaderContract.LeaderEntry.TABLE_NAME + " ("
                + LeaderContract.LeaderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LeaderContract.LeaderEntry.COLUMN_USER_ID + " TEXT NOT NULL,"
                + LeaderContract.LeaderEntry.COLUMN_USER_NAME + " TEXT,"
                + LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME + " TEXT,"
                + LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE + " INTEGER NOT NULL,"
                + LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS + " INTEGER NOT NULL,"
                + LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS + " TEXT,"
                + LeaderContract.LeaderEntry.COLUMN_EMAIL + " TEXT,"
                + LeaderContract.LeaderEntry.COLUMN_LOCATION + " TEXT,"
                + LeaderContract.LeaderEntry.COLUMN_WEBSITE + " TEXT,"
                + LeaderContract.LeaderEntry.COLUMN_PHOTO + " TEXT"
                + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_LEADER_TABLE);
    }
//Lifecycle end

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LeaderContract.LeaderEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
