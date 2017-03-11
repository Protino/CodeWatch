package io.github.protino.codewatch;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.remote.model.WakatimeData;
import io.github.protino.codewatch.remote.model.project.summary.SummaryResponse;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

@RunWith(AndroidJUnit4.class)
public class FetchWakatimeDataTest {

    private static final String FULL_NAME = "Gurupad Mamadapur";
    private static final String USERNAME = "Gurupad";
    private static final String CODEWATCH_ID = "9c0ef285-4370-40b2-92db-3ef67f3c26b8";
    private static final String TIMEZONE = "Asia/Calcutta";

    private FetchWakatimeData fetchWakatimeData =
            new FetchWakatimeData(InstrumentationRegistry.getTargetContext());

    @Test
    public void testExecute() throws IOException {
        WakatimeData wakatimeData = fetchWakatimeData.execute();

        assertNotNull(wakatimeData);

        //check leaders
        assertNotNull(wakatimeData.getLeadersResponse().getData());

        //check stats
        assertTrue(wakatimeData.getStatsResponse().getStatsData().getUsername().equals(USERNAME));

        //check projects
        String projectId = wakatimeData.getProjectsResponse().getProjectsList().get(0).getId();
        assertTrue(projectId.equals(CODEWATCH_ID));

        //check project summary
        assertTrue(wakatimeData.getSummaryResponse().get(projectId).getData().get(0).getRange().getTimezone()
                .equals(TIMEZONE));

        //check user data
        assertTrue(wakatimeData.getUserResponse().getProfileData().getFullName().equals(FULL_NAME));

    }

    @Test
    public void testProjectSummary() throws IOException {
        SummaryResponse response = fetchWakatimeData.fetchProjectSummary(CODEWATCH_ID);
        assertTrue(response.getData().get(0).getRange().getTimezone().equals(TIMEZONE));
    }

    @Test
    public void testLeadersData() throws IOException {
        assertNotNull(fetchWakatimeData.fetchLeaders());
    }
}
