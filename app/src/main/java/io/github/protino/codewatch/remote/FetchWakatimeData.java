package io.github.protino.codewatch.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.protino.codewatch.remote.interfaces.ApiInterface;
import io.github.protino.codewatch.remote.interfaces.PublicApiInterface;
import io.github.protino.codewatch.remote.model.AccessToken;
import io.github.protino.codewatch.remote.model.WakatimeData;
import io.github.protino.codewatch.remote.model.leaders.LeadersResponse;
import io.github.protino.codewatch.remote.model.project.ProjectsResponse;
import io.github.protino.codewatch.remote.model.project.summary.GenericSummaryData;
import io.github.protino.codewatch.remote.model.project.summary.GenericSummaryResponse;
import io.github.protino.codewatch.remote.model.project.summary.Project;
import io.github.protino.codewatch.remote.model.project.summary.ProjectSummaryResponse;
import io.github.protino.codewatch.remote.model.statistics.StatsResponse;
import io.github.protino.codewatch.remote.model.user.UserResponse;
import io.github.protino.codewatch.remote.retrofit.ServiceGenerator;
import io.github.protino.codewatch.utils.Constants;

import static io.github.protino.codewatch.utils.Constants.ACCESS_TOKEN_PREF_KEY;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class FetchWakatimeData {

    private final static String dateFormat = "yyyy-MM-dd";
    private ApiInterface apiInterface;
    private PublicApiInterface publicApiInterface;
    private String startDate;
    private String endDate;
    private String yesterday;

    private FetchWakatimeData() {
    }

    public FetchWakatimeData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accessTokenJson = sharedPreferences.getString(ACCESS_TOKEN_PREF_KEY, null);
        Gson gson = new Gson();
        String accessToken = gson.fromJson(accessTokenJson, AccessToken.class).getAccessToken();
        apiInterface = ServiceGenerator.createService(ApiInterface.class, accessToken);
        publicApiInterface = ServiceGenerator.createService(PublicApiInterface.class);
        setDates();
    }

    private void setDates() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        endDate = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, -7);
        startDate = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        yesterday = simpleDateFormat.format(calendar.getTime());
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

        StatsResponse statsResponse = fetchStats();
        wakatimeData.setStatsResponse(statsResponse);

        /*
        // TODO: 12-03-2017 Seperate this task from here
        HashMap<String, ProjectSummaryResponse> summaryResponseMap = new HashMap<>();
        ProjectSummaryResponse projectSummaryResponse;
        for (ProjectsData projectsData : projectsResponse.getProjectsList()) {
            String projectName = projectsData.getName();
            projectSummaryResponse = apiInterface.getProjectSummary(projectName, startDate, endDate).execute().body();
            summaryResponseMap.put(projectName, projectSummaryResponse);
        }
        wakatimeData.setSummaryResponse(summaryResponseMap);*/

        UserResponse userResponse = fetchUserDetails();
        wakatimeData.setUserResponse(userResponse);

        GenericSummaryResponse genericSummaryResponse = fetchGenericSummaryResponse();
        Map<String, Integer> projectStats;
        List<Map<String, Integer>> projectStatsList = new ArrayList<>();
        for (GenericSummaryData summaryData : genericSummaryResponse.getData()) {
            projectStats = new HashMap<>();
            for (Project project : summaryData.getProjects()) {
                projectStats.put(project.getName(), project.getTotalSeconds());
            }
            projectStatsList.add(projectStats);
        }
        wakatimeData.setProjectStatsList(projectStatsList);

        wakatimeData.setChangeInTotalSeconds(getChangeInTotalSeconds());
        return wakatimeData;
    }

    public UserResponse fetchUserDetails() throws IOException {
        return apiInterface.getUserProfileData().execute().body();
    }

    public ProjectSummaryResponse fetchProjectSummary(String project_id) throws IOException {
        return apiInterface.getProjectSummary(project_id, startDate, endDate).execute().body();
    }

    public LeadersResponse fetchLeaders() throws IOException {
        return apiInterface.getLeaders().execute().body();
    }

    public StatsResponse fetchStats() throws IOException {
        return apiInterface.getStats(Constants._7_DAYS).execute().body();
    }

    public ProjectsResponse fetchProjects() throws IOException {
        return apiInterface.getProjects().execute().body();
    }

    public int getChangeInTotalSeconds() throws IOException {
        GenericSummaryResponse response = apiInterface.getSummary(yesterday, endDate).execute().body();

        int yesterdaysTotalSeconds = response.getData().get(0).getGrandTotal().getTotalSeconds();
        int todaysTotalSeconds = response.getData().get(1).getGrandTotal().getTotalSeconds();

        return todaysTotalSeconds - yesterdaysTotalSeconds;
    }

    /*
        Note: Only the projects list matters for the project. But keeping that out
        to be as modular and independent as possible.
     */
    public GenericSummaryResponse fetchGenericSummaryResponse() throws IOException {
        return apiInterface.getSummary(startDate, endDate).execute().body();
    }
}
