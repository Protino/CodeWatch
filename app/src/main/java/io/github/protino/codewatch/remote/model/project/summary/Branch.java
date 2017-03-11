package io.github.protino.codewatch.remote.model.project.summary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Branch {

    @SerializedName("digital")
    @Expose
    private String digital;
    @SerializedName("hours")
    @Expose
    private Integer hours;
    @SerializedName("minutes")
    @Expose
    private Integer minutes;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("percent")
    @Expose
    private Double percent;
    @SerializedName("seconds")
    @Expose
    private Integer seconds;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("total_seconds")
    @Expose
    private Integer totalSeconds;

    public String getDigital() {
        return digital;
    }

    public void setDigital(String digital) {
        this.digital = digital;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

}
