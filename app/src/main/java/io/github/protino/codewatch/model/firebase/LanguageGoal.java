package io.github.protino.codewatch.model.firebase;

/**
 * @author Gurupad Mamadapur
 */

public class LanguageGoal {
    private String goalId;
    /**
     * Number of hours.
     */
    private int dailyGoal;
    private int[] progressSoFar; //time in seconds

    public LanguageGoal() {
        dailyGoal = -1;
        progressSoFar = new int[]{};
    }

    public LanguageGoal(String goalId,int dailyGoal, int[] progressSoFar) {
        this.goalId = goalId;
        this.dailyGoal = dailyGoal;
        this.progressSoFar = progressSoFar;
    }

    public int[] getProgressSoFar() {
        return progressSoFar;
    }

    public void setProgressSoFar(int[] progressSoFar) {
        this.progressSoFar = progressSoFar;
    }

    public int getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(int dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }
}
