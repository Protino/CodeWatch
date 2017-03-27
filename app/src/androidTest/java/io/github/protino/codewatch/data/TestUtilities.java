package io.github.protino.codewatch.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import io.github.protino.codewatch.utils.PollingCheckUtils;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your LeaderContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    public static final String TEST_USER_ID = "9w8e9qwdhq89wdy";
    public static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    private static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createLeaderValues() {
        ContentValues leaderValues = new ContentValues();
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE, 32);
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_USER_ID, TEST_USER_ID);
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_USER_NAME, "gurupad");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME, "Gurupad M");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_EMAIL, "gurupad@gmailc.om");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_LOCATION, "mars");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_PHOTO, "cad.co.com");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS, "nun");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_WEBSITE, "ac.oco.coo");
        leaderValues.put(LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS, 56487);
        return leaderValues;
    }

    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    public static ContentValues[] createBulkInsertLeaderValues() {
        String userId = TestUtilities.TEST_USER_ID;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, userId = i + userId.substring(1)) {
            ContentValues leaderValues = new ContentValues();
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_USER_ID, userId);
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS, 564654);
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_USER_NAME, "ASSAD");
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_WEBSITE, "ADWAD");
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_LOCATION, "Piipop");
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_EMAIL, "dwadwa^@Ead");
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS, "{}{#WDA#RE}}AD:::}{");
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE, 22342);
            leaderValues.put(LeaderContract.LeaderEntry.COLUMN_PHOTO, "sdf@.c.d.");
            returnContentValues[i] = leaderValues;
        }
        return returnContentValues;
    }

    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheckUtils(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}
