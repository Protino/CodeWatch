
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

package io.github.protino.codewatch.model.project.summary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectSummaryResponse {

    @SerializedName("available_branches")
    @Expose
    private List<String> availableBranches = null;
    @SerializedName("branches")
    @Expose
    private List<Object> branches = null;
    @SerializedName("data")
    @Expose
    private List<ProjectSummaryData> data = null;
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

    public List<ProjectSummaryData> getData() {
        return data;
    }

    public void setData(List<ProjectSummaryData> data) {
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
