
package io.github.protino.codewatch.model.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Language {

    @SerializedName("total_seconds")
    @Expose
    private Integer totalSeconds;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("percent")
    @Expose
    private Double percent;
    @SerializedName("hours")
    @Expose
    private Integer hours;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("minutes")
    @Expose
    private Integer minutes;
    @SerializedName("digital")
    @Expose
    private String digital;

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getDigital() {
        return digital;
    }

    public void setDigital(String digital) {
        this.digital = digital;
    }
}
