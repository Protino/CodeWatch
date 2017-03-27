package io.github.protino.codewatch.remote.model;

import java.util.List;
import java.util.Map;

import io.github.protino.codewatch.remote.model.project.ProjectsResponse;
import io.github.protino.codewatch.remote.model.statistics.StatsResponse;
import io.github.protino.codewatch.remote.model.user.UserResponse;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class WakatimeData {

    private UserResponse userResponse;
    private StatsResponse statsResponse;
    private Integer changeInTotalSeconds;
    private List<Map<String, Integer>> projectStatsList;
    private ProjectsResponse projectsResponse;

    public WakatimeData() {
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

    public List<Map<String, Integer>> getProjectStatsList() {
        return projectStatsList;
    }

    public void setProjectStatsList(List<Map<String, Integer>> projectStats) {
        this.projectStatsList = projectStats;
    }

    public Integer getChangeInTotalSeconds() {
        return changeInTotalSeconds;
    }

    public void setChangeInTotalSeconds(Integer changeInTotalSeconds) {
        this.changeInTotalSeconds = changeInTotalSeconds;
    }

    public ProjectsResponse getProjectsResponse() {
        return projectsResponse;
    }

    public void setProjectsResponse(ProjectsResponse projectsResponse) {
        this.projectsResponse = projectsResponse;
    }
}
