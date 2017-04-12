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

import io.github.protino.codewatch.model.AccessToken;
import io.github.protino.codewatch.model.WakatimeData;
import io.github.protino.codewatch.model.project.ProjectsResponse;
import io.github.protino.codewatch.model.project.summary.GenericSummaryData;
import io.github.protino.codewatch.model.project.summary.GenericSummaryResponse;
import io.github.protino.codewatch.model.project.summary.Project;
import io.github.protino.codewatch.model.project.summary.ProjectSummaryResponse;
import io.github.protino.codewatch.model.statistics.StatsResponse;
import io.github.protino.codewatch.model.user.UserResponse;
import io.github.protino.codewatch.remote.interfaces.ApiInterface;
import io.github.protino.codewatch.remote.retrofit.ServiceGenerator;
import io.github.protino.codewatch.utils.Constants;

import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_TOKEN;

/**
 * @author Gurupad Mamadapur
 */

public class FetchWakatimeData {

    private final static String dateFormat = "yyyy-MM-dd";
    private ApiInterface apiInterface;
    private String startDate;
    private String endDate;
    private String yesterday;

    private FetchWakatimeData() {
    }

    public FetchWakatimeData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accessTokenJson = sharedPreferences.getString(PREF_ACCESS_TOKEN, null);
        Gson gson = new Gson();
        String accessToken = gson.fromJson(accessTokenJson, AccessToken.class).getAccessToken();
        apiInterface = ServiceGenerator.createService(ApiInterface.class, accessToken);
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

        //User details
        UserResponse userResponse = fetchUserDetails();
        wakatimeData.setUserResponse(userResponse);

        //Stats
        fetchStatsOnly(wakatimeData);

        //Projects
        wakatimeData.setProjectsResponse(fetchProjectsList());
        return wakatimeData;
    }

    public void fetchStatsOnly(WakatimeData wakatimeData) throws IOException {
        if (apiInterface == null) {
            return;
        }
        StatsResponse statsResponse = fetchStats();
        wakatimeData.setStatsResponse(statsResponse);

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

        wakatimeData.setTodaysTotalSeconds(getTodaysTotalSeconds());
    }

    public UserResponse fetchUserDetails() throws IOException {
        return apiInterface.getUserProfileData().execute().body();
    }

    public ProjectSummaryResponse fetchProjectSummary(String projectName) throws IOException {
        return apiInterface.getProjectSummary(projectName, startDate, endDate).execute().body();
    }

    public StatsResponse fetchStats() throws IOException {
        return apiInterface.getStats(Constants._7_DAYS).execute().body();
    }

    public ProjectsResponse fetchProjects() throws IOException {
        return apiInterface.getProjects().execute().body();
    }

    public Integer getTodaysTotalSeconds() throws IOException {
        GenericSummaryResponse response = apiInterface.getSummary(yesterday, endDate).execute().body();
        int todaysTotalSeconds = response.getData().get(1).getGrandTotal().getTotalSeconds();
        return todaysTotalSeconds;
    }

    /*
        Note: Only the projects list matters for the project. But keeping that out
        to be as modular and independent as possible.
     */
    public GenericSummaryResponse fetchGenericSummaryResponse() throws IOException {
        return apiInterface.getSummary(startDate, endDate).execute().body();
    }

    public ProjectsResponse fetchProjectsList() throws IOException {
        return apiInterface.getProjects().execute().body();
    }
}
