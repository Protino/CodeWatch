package io.github.protino.codewatch.remote.model;

import java.util.HashMap;

import io.github.protino.codewatch.remote.model.leaders.LeadersResponse;
import io.github.protino.codewatch.remote.model.project.ProjectsResponse;
import io.github.protino.codewatch.remote.model.project.summary.SummaryResponse;
import io.github.protino.codewatch.remote.model.statistics.StatsResponse;
import io.github.protino.codewatch.remote.model.user.UserResponse;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class WakatimeData {

    private UserResponse userResponse;
    private StatsResponse statsResponse;
    private ProjectsResponse projectsResponse;
    private HashMap<String, SummaryResponse> summaryResponse;
    private LeadersResponse leadersResponse;

    public WakatimeData() {
    }

    public WakatimeData(UserResponse userResponse, StatsResponse statsResponse, ProjectsResponse projectsResponse, HashMap<String, SummaryResponse> summaryResponse, LeadersResponse leadersResponse) {
        this.userResponse = userResponse;
        this.statsResponse = statsResponse;
        this.projectsResponse = projectsResponse;
        this.summaryResponse = summaryResponse;
        this.leadersResponse = leadersResponse;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(UserResponse userResponse) {
        this.userResponse = userResponse;
    }

    public StatsResponse getStatsResponse() {
        return statsResponse;
    }

    public void setStatsResponse(StatsResponse statsResponse) {
        this.statsResponse = statsResponse;
    }

    public ProjectsResponse getProjectsResponse() {
        return projectsResponse;
    }

    public void setProjectsResponse(ProjectsResponse projectsResponse) {
        this.projectsResponse = projectsResponse;
    }

    public HashMap<String, SummaryResponse> getSummaryResponse() {
        return summaryResponse;
    }

    public void setSummaryResponse(HashMap<String, SummaryResponse> summaryResponse) {
        this.summaryResponse = summaryResponse;
    }

    public LeadersResponse getLeadersResponse() {
        return leadersResponse;
    }

    public void setLeadersResponse(LeadersResponse leadersResponse) {
        this.leadersResponse = leadersResponse;
    }
}
