
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
