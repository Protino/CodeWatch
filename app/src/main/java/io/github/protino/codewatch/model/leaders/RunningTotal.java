
package io.github.protino.codewatch.model.leaders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RunningTotal {

    @SerializedName("daily_average")
    @Expose
    private Integer dailyAverage;
    @SerializedName("human_readable_daily_average")
    @Expose
    private String humanReadableDailyAverage;
    @SerializedName("human_readable_total")
    @Expose
    private String humanReadableTotal;
    @SerializedName("languages")
    @Expose
    private List<Language> languages = null;
    @SerializedName("modified_at")
    @Expose
    private String modifiedAt;
    @SerializedName("total_seconds")
    @Expose
    private Integer totalSeconds;

    public Integer getDailyAverage() {
        return dailyAverage;
    }

    public void setDailyAverage(Integer dailyAverage) {
        this.dailyAverage = dailyAverage;
    }

    public String getHumanReadableDailyAverage() {
        return humanReadableDailyAverage;
    }

    public void setHumanReadableDailyAverage(String humanReadableDailyAverage) {
        this.humanReadableDailyAverage = humanReadableDailyAverage;
    }

    public String getHumanReadableTotal() {
        return humanReadableTotal;
    }

    public void setHumanReadableTotal(String humanReadableTotal) {
        this.humanReadableTotal = humanReadableTotal;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

}
