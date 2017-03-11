package io.github.protino.codewatch.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.github.protino.codewatch.remote.model.AccessToken;
import io.github.protino.codewatch.remote.model.WakatimeData;
import io.github.protino.codewatch.remote.model.leaders.LeadersResponse;
import io.github.protino.codewatch.remote.model.project.ProjectsData;
import io.github.protino.codewatch.remote.model.project.ProjectsResponse;
import io.github.protino.codewatch.remote.model.project.summary.SummaryResponse;
import io.github.protino.codewatch.remote.model.statistics.StatsResponse;
import io.github.protino.codewatch.remote.model.user.UserResponse;

import static io.github.protino.codewatch.remote.Constants.ACCESS_TOKEN_PREF_KEY;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class FetchWakatimeData {

    private final static String dateFormat = "yyyy-MM-dd";
    private static final String LOG_TAG = FetchWakatimeData.class.getSimpleName();
    private ApiInterface apiInterface;
    private PublicApiInterface publicApiInterface;
    private String startDate;
    private String endDate;

    private FetchWakatimeData() {
    }

    public FetchWakatimeData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accessTokenJson = sharedPreferences.getString(ACCESS_TOKEN_PREF_KEY, null);
        Gson gson = new Gson();
        String accessToken = gson.fromJson(accessTokenJson, AccessToken.class).getAccessToken();
        apiInterface = ServiceGenerator.createService(ApiInterface.class, accessToken);
        publicApiInterface = ServiceGenerator.createService(PublicApiInterface.class);
        setStartAndEndDate();
    }

    private void setStartAndEndDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        endDate = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, -7);
        startDate = simpleDateFormat.format(calendar.getTime());
        Log.d(LOG_TAG, "setStartAndEndDate: " + startDate + " " + endDate);
    }

    /**
     * Fetches all the relevant data from Wakatime API
     *
     * @return {@link WakatimeData} if successful els1e null
     */
    public WakatimeData execute() throws IOException {
        if (apiInterface == null) {
            return null;
        }
        WakatimeData wakatimeData = new WakatimeData();

        StatsResponse statsResponse = apiInterface.getStats(Constants._7_DAYS).execute().body();
        wakatimeData.setStatsResponse(statsResponse);

        ProjectsResponse projectsResponse = apiInterface.getProjects().execute().body();
        wakatimeData.setProjectsResponse(projectsResponse);

        HashMap<String, SummaryResponse> summaryResponseMap = new HashMap<>();
        SummaryResponse summaryResponse;
        for (ProjectsData projectsData : projectsResponse.getProjectsList()) {
            String projectId = projectsData.getId();
            summaryResponse = apiInterface.getProjectSummary(projectId, startDate, endDate).execute().body();
            summaryResponseMap.put(projectId, summaryResponse);
        }
        wakatimeData.setSummaryResponse(summaryResponseMap);

        LeadersResponse leadersResponse = publicApiInterface.getLeaders().execute().body();
        wakatimeData.setLeadersResponse(leadersResponse);

        UserResponse userResponse = apiInterface.getUserProfileData().execute().body();
        wakatimeData.setUserResponse(userResponse);

        return wakatimeData;
    }

    public SummaryResponse fetchProjectSummary(String project_id) throws IOException {
        return apiInterface.getProjectSummary(project_id, startDate, endDate).execute().body();
    }

    public LeadersResponse fetchLeaders() throws IOException {
        return apiInterface.getLeaders().execute().body();
    }
}
