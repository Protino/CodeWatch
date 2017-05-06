
/*
 * Copyright 2017 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.codewatch.model.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StatsData {

    @SerializedName("modified_at")
    @Expose
    private String modifiedAt;
    @SerializedName("is_already_updating")
    @Expose
    private Boolean isAlreadyUpdating;
    @SerializedName("human_readable_total")
    @Expose
    private String humanReadableTotal;
    @SerializedName("range")
    @Expose
    private String range;
    @SerializedName("human_readable_daily_average")
    @Expose
    private String humanReadableDailyAverage;
    @SerializedName("project")
    @Expose
    private String project;
    @SerializedName("best_day")
    @Expose
    private BestDay bestDay;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("total_seconds")
    @Expose
    private Integer totalSeconds;
    @SerializedName("holidays")
    @Expose
    private Integer holidays;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("daily_average")
    @Expose
    private Integer dailyAverage;
    @SerializedName("is_up_to_date")
    @Expose
    private Boolean isUpToDate;
    @SerializedName("projects")
    @Expose
    private List<Project> projects = null;
    @SerializedName("days_minus_holidays")
    @Expose
    private Integer daysMinusHolidays;
    @SerializedName("timeout")
    @Expose
    private Integer timeout;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("writes_only")
    @Expose
    private Boolean writesOnly;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("languages")
    @Expose
    private List<Language> languages = null;
    @SerializedName("operating_systems")
    @Expose
    private List<OperatingSystem> operatingSystems = null;
    @SerializedName("days_including_holidays")
    @Expose
    private Integer daysIncludingHolidays;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("editors")
    @Expose
    private List<Editor> editors = null;
    @SerializedName("is_stuck")
    @Expose
    private Boolean isStuck;

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Boolean getIsAlreadyUpdating() {
        return isAlreadyUpdating;
    }

    public void setIsAlreadyUpdating(Boolean isAlreadyUpdating) {
        this.isAlreadyUpdating = isAlreadyUpdating;
    }

    public String getHumanReadableTotal() {
        return humanReadableTotal;
    }

    public void setHumanReadableTotal(String humanReadableTotal) {
        this.humanReadableTotal = humanReadableTotal;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getHumanReadableDailyAverage() {
        return humanReadableDailyAverage;
    }

    public void setHumanReadableDailyAverage(String humanReadableDailyAverage) {
        this.humanReadableDailyAverage = humanReadableDailyAverage;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public BestDay getBestDay() {
        return bestDay;
    }

    public void setBestDay(BestDay bestDay) {
        this.bestDay = bestDay;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public Integer getHolidays() {
        return holidays;
    }

    public void setHolidays(Integer holidays) {
        this.holidays = holidays;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getDailyAverage() {
        return dailyAverage;
    }

    public void setDailyAverage(Integer dailyAverage) {
        this.dailyAverage = dailyAverage;
    }

    public Boolean getIsUpToDate() {
        return isUpToDate;
    }

    public void setIsUpToDate(Boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Integer getDaysMinusHolidays() {
        return daysMinusHolidays;
    }

    public void setDaysMinusHolidays(Integer daysMinusHolidays) {
        this.daysMinusHolidays = daysMinusHolidays;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Boolean getWritesOnly() {
        return writesOnly;
    }

    public void setWritesOnly(Boolean writesOnly) {
        this.writesOnly = writesOnly;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<OperatingSystem> getOperatingSystems() {
        return operatingSystems;
    }

    public void setOperatingSystems(List<OperatingSystem> operatingSystems) {
        this.operatingSystems = operatingSystems;
    }

    public Integer getDaysIncludingHolidays() {
        return daysIncludingHolidays;
    }

    public void setDaysIncludingHolidays(Integer daysIncludingHolidays) {
        this.daysIncludingHolidays = daysIncludingHolidays;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<Editor> getEditors() {
        return editors;
    }

    public void setEditors(List<Editor> editors) {
        this.editors = editors;
    }

    public Boolean getIsStuck() {
        return isStuck;
    }

    public void setIsStuck(Boolean isStuck) {
        this.isStuck = isStuck;
    }

}
