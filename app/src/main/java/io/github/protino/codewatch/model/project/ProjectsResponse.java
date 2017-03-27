
package io.github.protino.codewatch.model.project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectsResponse {

    @SerializedName("data")
    @Expose
    private List<ProjectsData> projectsList = null;

    public List<ProjectsData> getProjectsList() {
        return projectsList;
    }

    public void setProjectsList(List<ProjectsData> projectsList) {
        this.projectsList = projectsList;
    }

}
