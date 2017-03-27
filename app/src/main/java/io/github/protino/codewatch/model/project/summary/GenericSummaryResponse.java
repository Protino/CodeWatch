
package io.github.protino.codewatch.model.project.summary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericSummaryResponse {

    @SerializedName("data")
    @Expose
    private List<GenericSummaryData> data = null;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("start")
    @Expose
    private String start;

    public List<GenericSummaryData> getData() {
        return data;
    }

    public void setData(List<GenericSummaryData> data) {
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
