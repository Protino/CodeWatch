
package io.github.protino.codewatch.remote.model.project.summary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericSummaryData {

    @SerializedName("editors")
    @Expose
    private List<Editor> editors = null;
    @SerializedName("entities")
    @Expose
    private List<Entity> entities = null;
    @SerializedName("grand_total")
    @Expose
    private GrandTotal grandTotal;
    @SerializedName("languages")
    @Expose
    private List<Language> languages = null;
    @SerializedName("operating_systems")
    @Expose
    private List<OperatingSystem> operatingSystems = null;
    @SerializedName("projects")
    @Expose
    private List<Project> projects = null;
    @SerializedName("range")
    @Expose
    private Range range;

    public List<Editor> getEditors() {
        return editors;
    }

    public void setEditors(List<Editor> editors) {
        this.editors = editors;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public GrandTotal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(GrandTotal grandTotal) {
        this.grandTotal = grandTotal;
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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

}
