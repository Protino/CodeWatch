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
