package io.github.protino.codewatch.model.firebase;

import java.util.List;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Project {

    private String id;
    private String name;
    private String publicUrl;
    private List<Integer> timeSpent;
    private List<CustomPair> languageList;
    private List<CustomPair> osPairList;
    private List<CustomPair> editorPaiList;
    /* CodeWatch data */
    private Integer deadline;       // time since epoch in seconds!
    private Integer dailyHourGoal;  // in seconds

    public Project() {

    }

    public Project(String id, List<Integer> timeSpent, List<CustomPair> languageList,
                   List<CustomPair> osPairList, List<CustomPair> editorPaiList,
                   Integer deadline, Integer dailyHourGoal) {
        this.id = id;
        this.timeSpent = timeSpent;
        this.languageList = languageList;
        this.osPairList = osPairList;
        this.editorPaiList = editorPaiList;
        this.deadline = deadline;
        this.dailyHourGoal = dailyHourGoal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public List<Integer> getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(List<Integer> timeSpent) {
        this.timeSpent = timeSpent;
    }

    public List<CustomPair> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<CustomPair> languageList) {
        this.languageList = languageList;
    }

    public List<CustomPair> getOsPairList() {
        return osPairList;
    }

    public void setOsPairList(List<CustomPair> osPairList) {
        this.osPairList = osPairList;
    }

    public List<CustomPair> getEditorPaiList() {
        return editorPaiList;
    }

    public void setEditorPaiList(List<CustomPair> editorPaiList) {
        this.editorPaiList = editorPaiList;
    }

    public Integer getDailyHourGoal() {
        return dailyHourGoal;
    }

    public void setDailyHourGoal(Integer dailyHourGoal) {
        this.dailyHourGoal = dailyHourGoal;
    }

    public Integer getDeadline() {
        return deadline;
    }

    public void setDeadline(Integer deadline) {
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
