package io.github.protino.codewatch.remote.model.firebase;

import java.util.List;
import java.util.Map;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Stats {


    private Boolean isUpToDate;
    private String startDate;
    private String endDate;
    private String bestDayDate;
    private Integer bestDaySeconds;
    private Integer dailyAverageSeconds;
    private Integer totalSeconds;
    private Integer todaysTotalSeconds;
    private Integer changeInTotalSeconds;
    private List<Map<String, Integer>> projectPairList;
    private List<CustomPair> languagePairList;
    private List<CustomPair> osPairList;
    private List<CustomPair> editorPairList;

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

    public Integer getChangeInTotalSeconds() {
        return changeInTotalSeconds;
    }

    public void setChangeInTotalSeconds(Integer changeInTotalSeconds) {
        this.changeInTotalSeconds = changeInTotalSeconds;
    }

    public List<Map<String, Integer>> getProjectPairList() {
        return projectPairList;
    }

    public void setProjectPairList(List<Map<String, Integer>> projectPairList) {
        this.projectPairList = projectPairList;
    }

    public List<CustomPair> getLanguagePairList() {
        return languagePairList;
    }

    public void setLanguagePairList(List<CustomPair> languagePairList) {
        this.languagePairList = languagePairList;
    }

    public List<CustomPair> getOsPairList() {
        return osPairList;
    }

    public void setOsPairList(List<CustomPair> osPairList) {
        this.osPairList = osPairList;
    }

    public List<CustomPair> getEditorPairList() {
        return editorPairList;
    }

    public void setEditorPairList(List<CustomPair> editorPairList) {
        this.editorPairList = editorPairList;
    }
}
