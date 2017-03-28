package io.github.protino.codewatch.remote.model.firebase;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectGoal {
    /**
     * time since epoch in seoncds
     */
    private int deadline; //time since epoch in seconds
    /**
     * Number of seconds that must be spent on the project daily,
     */
    private int daily;

    public ProjectGoal() {
        //set goals to -1 to imply no goals are set by the user
        deadline = -1;
        daily = -1;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getDaily() {
        return daily;
    }

    public void setDaily(int daily) {
        this.daily = daily;
    }
}
