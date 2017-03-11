
package io.github.protino.codewatch.remote.model.project.summary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SummaryResponse {

    @SerializedName("available_branches")
    @Expose
    private List<String> availableBranches = null;
    @SerializedName("branches")
    @Expose
    private List<Object> branches = null;
    @SerializedName("data")
    @Expose
    private List<SummaryData> data = null;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("start")
    @Expose
    private String start;

    public List<String> getAvailableBranches() {
        return availableBranches;
    }

    public void setAvailableBranches(List<String> availableBranches) {
        this.availableBranches = availableBranches;
    }

    public List<Object> getBranches() {
        return branches;
    }

    public void setBranches(List<Object> branches) {
        this.branches = branches;
    }

    public List<SummaryData> getData() {
        return data;
    }

    public void setData(List<SummaryData> data) {
        this.data = data;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

}