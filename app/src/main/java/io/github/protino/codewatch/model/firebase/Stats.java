package io.github.protino.codewatch.model.firebase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Stats implements Serializable{


    private Boolean isUpToDate;
    private String startDate;
    private String endDate;
    private String bestDayDate;
    private Integer bestDaySeconds;
    private Integer dailyAverageSeconds;
    private Integer totalSeconds;
    private Integer todaysTotalSeconds;
    private List<Map<String, Integer>> projectPairList;
    private Map<String, Integer> languagesMap;
    private Map<String, Integer> osMap;
    private Map<String, Integer> editorsMap;

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
