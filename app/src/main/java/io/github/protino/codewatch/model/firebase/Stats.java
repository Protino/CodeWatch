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

package io.github.protino.codewatch.model.firebase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Stats implements Serializable{

    public Boolean isUpToDate;
    public String startDate;
    public String endDate;
    public String bestDayDate;
    public Integer bestDaySeconds;
    public Integer dailyAverageSeconds;
    public Integer totalSeconds;
    public Integer todaysTotalSeconds;
    public List<Map<String, Integer>> projectPairList;
    public Map<String, Integer> languagesMap;
    public Map<String, Integer> osMap;
    public Map<String, Integer> editorsMap;

    public Stats() {
    }

    public Boolean getUpToDate() {
        return isUpToDate;
    }

    public void setUpToDate(Boolean upToDate) {
        isUpToDate = upToDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getBestDayDate() {
        return bestDayDate;
    }

    public void setBestDayDate(String bestDayDate) {
        this.bestDayDate = bestDayDate;
    }

    public Integer getBestDaySeconds() {
        return bestDaySeconds;
    }

    public void setBestDaySeconds(Integer bestDaySeconds) {
        this.bestDaySeconds = bestDaySeconds;
    }

    public Integer getDailyAverageSeconds() {
        return dailyAverageSeconds;
    }

    public void setDailyAverageSeconds(Integer dailyAverageSeconds) {
        this.dailyAverageSeconds = dailyAverageSeconds;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public Integer getTodaysTotalSeconds() {
        return todaysTotalSeconds;
    }

    public void setTodaysTotalSeconds(Integer todaysTotalSeconds) {
        this.todaysTotalSeconds = todaysTotalSeconds;
    }

    public List<Map<String, Integer>> getProjectPairList() {
        return projectPairList;
    }

    public void setProjectPairList(List<Map<String, Integer>> projectPairList) {
        this.projectPairList = projectPairList;
    }

    public Map<String, Integer> getLanguagesMap() {
        return languagesMap;
    }

    public void setLanguagesMap(Map<String, Integer> languagesMap) {
        this.languagesMap = languagesMap;
    }

    public Map<String, Integer> getOsMap() {
        return osMap;
    }

    public void setOsMap(Map<String, Integer> osMap) {
        this.osMap = osMap;
    }

    public Map<String, Integer> getEditorsMap() {
        return editorsMap;
    }

    public void setEditorsMap(Map<String, Integer> editorsMap) {
        this.editorsMap = editorsMap;
    }
}
