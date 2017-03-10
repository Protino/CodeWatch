
package io.github.protino.codewatch.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BestDay {

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("total_seconds")
    @Expose
    private Integer totalSeconds;
    @SerializedName("modified_at")
    @Expose
    private String modifiedAt;
    @SerializedName("id")
    @Expose
    private String id;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
