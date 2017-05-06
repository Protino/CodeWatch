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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import static io.github.protino.codewatch.data.LeaderContract.LeaderEntry.TABLE_NAME;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(LeaderDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        mContext.deleteDatabase(LeaderDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new LeaderDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        c.moveToNext(); //to skip android_metadata table

        assertTrue("Error: Your database was created without leader table", c.getString(0).equals(TABLE_NAME));

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> leaderColumnHashSet = new HashSet<>();
        leaderColumnHashSet.add(LeaderContract.LeaderEntry._ID);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_EMAIL);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_PHOTO);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_USER_ID);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_USER_NAME);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_RANK);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_WEBSITE);
        leaderColumnHashSet.add(LeaderContract.LeaderEntry.COLUMN_LOCATION);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            leaderColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required leader entry columns",
                leaderColumnHashSet.isEmpty());
        c.close();
        db.close();
    }

    public void testLeaderTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new LeaderDbHelper(mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues contentValues = TestUtilities.createLeaderValues();

        // Insert ContentValues into database and get a row ID back
        long rowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        assertTrue(rowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                null, null, null, null, null, null);

        //Validate the cursor
        TestUtilities.validateCursor("Invalid data returned", cursor, contentValues);

        assertFalse("Database returned more than one row", cursor.moveToNext());
        // Finally, close the cursor and database
        cursor.close();
        sqLiteDatabase.close();
    }
}
