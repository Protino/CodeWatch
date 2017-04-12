package io.github.protino.codewatch.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    }

}
