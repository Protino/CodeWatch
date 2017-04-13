package io.github.protino.codewatch.model.firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectGoal {

    private String projectId;

    private String projectName;

    private long deadline;

    private long startDate;

    /**
     * Number of hours that must be spent on the project daily,
     */
    private int daily;

    private List<Integer> progressSoFar; //time

    public ProjectGoal() {
        //set goals to -1 to imply no goals are set by the user
        deadline = -1;
        daily = -1;
        progressSoFar = new ArrayList<>();
        startDate = -1;
    }

    public ProjectGoal(String projectId, String projectName, long deadline, long startDate, int daily, List<Integer> progressSoFar) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.deadline = deadline;
        this.startDate = startDate;
        this.daily = daily;
        this.progressSoFar = progressSoFar;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public int getDailyGoal() {
        return daily;
    }

    public void setDaily(int daily) {
        this.daily = daily;
    }

    public List<Integer> getProgressSoFar() {
        return progressSoFar;
    }

    public void setProgressSoFar(List<Integer> progressSoFar) {
        this.progressSoFar = progressSoFar;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
}
