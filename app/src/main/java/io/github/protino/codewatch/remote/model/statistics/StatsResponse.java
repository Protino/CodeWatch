package io.github.protino.codewatch.remote.model.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatsResponse {

    @SerializedName("data")
    @Expose
    private StatsData statsData;

    public StatsData getStatsData() {
        return statsData;
    }

    public void setStatsData(StatsData statsData) {
        this.statsData = statsData;
    }
}
