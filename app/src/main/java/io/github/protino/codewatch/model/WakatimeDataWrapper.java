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

package io.github.protino.codewatch.model;

import java.util.List;
import java.util.Map;

import io.github.protino.codewatch.model.project.ProjectsResponse;
import io.github.protino.codewatch.model.statistics.StatsResponse;
import io.github.protino.codewatch.model.user.UserResponse;

/**
 * Created by Gurupad Mamadapur on 10-03-2017.
 */

public class WakatimeDataWrapper {

    private UserResponse userResponse;
    private StatsResponse statsResponse;
    private Integer todaysTotalSeconds;
    //private Integer previousDailyAverage; //Not supported by api
    private List<Map<String, Integer>> projectStatsList;
    private ProjectsResponse projectsResponse;

    public WakatimeDataWrapper() {
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

    public ProjectsResponse getProjectsResponse() {
        return projectsResponse;
    }

    public void setProjectsResponse(ProjectsResponse projectsResponse) {
        this.projectsResponse = projectsResponse;
    }

    public Integer getTodaysTotalSeconds() {
        return todaysTotalSeconds;
    }

    public void setTodaysTotalSeconds(Integer todaysTotalSeconds) {
        this.todaysTotalSeconds = todaysTotalSeconds;
    }
}
