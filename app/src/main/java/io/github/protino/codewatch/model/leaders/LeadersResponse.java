
package io.github.protino.codewatch.model.leaders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LeadersResponse {

    @SerializedName("data")
    @Expose
    private List<LeadersData> data = null;
    @SerializedName("language")
    @Expose
    private Object language;
    @SerializedName("modified_at")
    @Expose
    private String modifiedAt;
    @SerializedName("range")
    @Expose
    private String range;

    public List<LeadersData> getData() {
        return data;
    }

    public void setData(List<LeadersData> data) {
        this.data = data;
    }

    public Object getLanguage() {
        return language;
    }

    public void setLanguage(Object language) {
        this.language = language;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

}
