package io.github.protino.codewatch.remote;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.protino.codewatch.data.LeaderContract;

/**
 * @author Gurupad Mamadapur
 */

@RunWith(AndroidJUnit4.class)
public class FetchLeaderBoardDataTest {

    private FetchLeaderBoardData fetchLeaderBoardData;
    private Context context;

    @Before
    public void init() {
        context = InstrumentationRegistry.getTargetContext();
        fetchLeaderBoardData = new FetchLeaderBoardData(context);
    }

    @Test
    public void completeTest() {
        Assert.assertTrue(fetchLeaderBoardData.execute());

        Cursor cursor = context.getContentResolver().query(
                LeaderContract.LeaderEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertNotNull(cursor);

        Assert.assertTrue(cursor.moveToFirst());

        Assert.assertNotNull(cursor.getString(cursor.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_USER_ID)));
    }

    @Test
    public void testFetchUserRank(){
        Assert.assertTrue(fetchLeaderBoardData.fetchUserRank()!=-1);
    }
}
