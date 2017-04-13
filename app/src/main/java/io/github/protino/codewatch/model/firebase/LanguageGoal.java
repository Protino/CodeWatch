package io.github.protino.codewatch.model.firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gurupad Mamadapur
 */

public class LanguageGoal {
    private String goalId;
    /**
     * Number of hours.
     */
    private int dailyGoal;
    private List<Integer> progressSoFar; //time in seconds

    public LanguageGoal() {
        dailyGoal = -1;
        progressSoFar = new ArrayList<>();
    }

    public LanguageGoal(String goalId, int dailyGoal, List<Integer> progressSoFar) {
        this.goalId = goalId;
        this.dailyGoal = dailyGoal;
        this.progressSoFar = progressSoFar;
    }

    public List<Integer> getProgressSoFar() {
        return progressSoFar;
    }

    public void setProgressSoFar(List<Integer> progressSoFar) {
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
