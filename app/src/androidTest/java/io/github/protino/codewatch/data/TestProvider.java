package io.github.protino.codewatch.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import static io.github.protino.codewatch.data.TestUtilities.BULK_INSERT_RECORDS_TO_INSERT;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();


    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null,
                null);

        Cursor cursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Leader table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
//    public void testProviderRegistry() {
//        PackageManager pm = mContext.getPackageManager();
//
//        // We define the component name based on the package name from the context and the
//        // WeatherProvider class.
//        ComponentName componentName = new ComponentName(mContext.getPackageName(),
//                WeatherProvider.class.getName());
//        try {
//            // Fetch the provider info using the component name from the PackageManager
//            // This throws an exception if the provider isn't registered.
//            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
//
//            // Make sure that the registered authority matches the authority from the Contract.
//            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
//                    " instead of authority: " + WeatherContract.CONTENT_AUTHORITY,
//                    providerInfo.authority, WeatherContract.CONTENT_AUTHORITY);
//        } catch (PackageManager.NameNotFoundException e) {
//            // I guess the provider isn't registered correctly.
//            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
//                    false);
//        }
//    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://io.github.protino.codewatch/leader/
        String type = mContext.getContentResolver().getType(LeaderContract.LeaderEntry.CONTENT_URI);

        // vnd.android.cursor.dir/io.github.protino.codewatch/leader
        assertEquals("Error: the LeaderEntry CONTENT_URI should return LeaderEntry.CONTENT_TYPE",
                LeaderContract.LeaderEntry.CONTENT_TYPE, type);

        String userId = "8df8f8dd8d58d7d87";
        // content://io.github.protino.codewatch/leader/userId
        type = mContext.getContentResolver().getType(LeaderContract.LeaderEntry.buildProfileUri(userId));
        // vnd.android.cursor.item/io.github.protino.codewatch/leader/1419120000
        assertEquals("Error: the LeaderEntry CONTENT_URI with userId should return LeaderEntry.CONTENT_ITEM_TYPE",
                LeaderContract.LeaderEntry.CONTENT_ITEM_TYPE, type);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    public void testBasicLeaderQuery() {
        // insert our test records into the database
        LeaderDbHelper dbHelper = new LeaderDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createLeaderValues();

        long leaderRowId = db.insert(LeaderContract.LeaderEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert LeaderEntry into the Database", leaderRowId != -1);
        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLeaderQuery", cursor, testValues);
    }

    public void testUpdateLeader() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createLeaderValues();

        Uri Uri = mContext.getContentResolver().
                insert(LeaderContract.LeaderEntry.CONTENT_URI, values);
        long leaderRowId = ContentUris.parseId(Uri);

        // Verify we got a row back.
        assertTrue(leaderRowId != -1);
        Log.d(LOG_TAG, "New row id: " + leaderRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LeaderContract.LeaderEntry._ID, leaderRowId);
        updatedValues.put(LeaderContract.LeaderEntry.COLUMN_USER_NAME, "Bob");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor leaderCursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        leaderCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                LeaderContract.LeaderEntry.CONTENT_URI, updatedValues, LeaderContract.LeaderEntry._ID + "= ?",
                new String[]{Long.toString(leaderRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        tco.waitForNotificationOrFail();

        leaderCursor.unregisterContentObserver(tco);
        leaderCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null,
                LeaderContract.LeaderEntry._ID + " = " + leaderRowId,
                null,
                null
        );

        TestUtilities.validateCursor("testUpdateLeader.  Error validating leader entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    // Make sure we can still delete after adding/updating stuff
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createLeaderValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LeaderContract.LeaderEntry.CONTENT_URI, true, tco);
        Uri leaderUri = mContext.getContentResolver().insert(LeaderContract.LeaderEntry.CONTENT_URI, testValues);

        // test
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long leaderRowId = ContentUris.parseId(leaderUri);

        // Verify we got a row back.
        assertTrue(leaderRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LeaderEntry.",
                cursor, testValues);

        // To test profile uri, delete all data query for specific userId
        deleteAllRecordsFromProvider();
        //insert again
        mContext.getContentResolver().insert(LeaderContract.LeaderEntry.CONTENT_URI, testValues);
        cursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.buildProfileUri(TestUtilities.TEST_USER_ID),
                null,
                null,
                null,
                null
        );
        assertTrue(cursor.getCount() == 1);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating query with user id", cursor, testValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver contentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LeaderContract.LeaderEntry.CONTENT_URI, true, contentObserver);

        deleteAllRecordsFromProvider();

        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);
    }

    public void testBulkInsert() {

        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertLeaderValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver contentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                LeaderContract.LeaderEntry.CONTENT_URI, true, contentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                LeaderContract.LeaderEntry.CONTENT_URI, bulkInsertContentValues);

        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null,
                null,
                null,
                LeaderContract.LeaderEntry.COLUMN_USER_ID + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);
        cursor.close();
    }
}
