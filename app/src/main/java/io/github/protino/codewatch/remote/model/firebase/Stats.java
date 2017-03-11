package io.github.protino.codewatch.remote.model.firebase;

import java.util.List;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Stats {

    private String bestDayDate;
    private Integer bestDaySeconds;
    private Integer dailyAverageSeconds;
    private Integer totalSeconds;
    private Integer todaysTotalSeconds;
    private Integer changeInTotalSeconds;

    private List<CustomPair> projectPairList;
    private List<CustomPair> languagePairList;
    private List<CustomPair> osPairList;
    private List<CustomPair> editorPairList;

    public Stats() {
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

    public List<CustomPair> getProjectPairList() {
        return projectPairList;
    }

    public void setProjectPairList(List<CustomPair> projectPairList) {
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
