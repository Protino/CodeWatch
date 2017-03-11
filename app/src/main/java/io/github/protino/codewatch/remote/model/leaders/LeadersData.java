
package io.github.protino.codewatch.remote.model.leaders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeadersData {

    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("running_total")
    @Expose
    private RunningTotal runningTotal;
    @SerializedName("user")
    @Expose
    private User user;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public RunningTotal getRunningTotal() {
        return runningTotal;
    }

    public void setRunningTotal(RunningTotal runningTotal) {
        this.runningTotal = runningTotal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
