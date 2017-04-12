package io.github.protino.codewatch.model.firebase;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectGoal {

    private String projectId;

    private String projectName;

    /**
     * time since epoch in seconds
     */
    private int deadline; //time since epoch in seconds

    private int startDate;

    /**
     * Number of hours that must be spent on the project daily,
     */
    private int daily;

    private int[] progressSoFar; //time

    public ProjectGoal() {
        //set goals to -1 to imply no goals are set by the user
        deadline = -1;
        daily = -1;
        progressSoFar = new int[]{};
        startDate = -1;
    }

    public ProjectGoal(String projectId, String projectName, int deadline, int startDate, int daily, int[] progressSoFar) {
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

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getDailyGoal() {
        return daily;
    }

    public void setDaily(int daily) {
        this.daily = daily;
    }

    public int[] getProgressSoFar() {
        return progressSoFar;
    }

    public void setProgressSoFar(int[] progressSoFar) {
        this.progressSoFar = progressSoFar;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }
}
