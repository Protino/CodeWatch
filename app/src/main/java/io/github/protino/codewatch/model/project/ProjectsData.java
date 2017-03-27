
package io.github.protino.codewatch.model.project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectsData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("public_url")
    @Expose
    private String publicUrl;
    @SerializedName("repository")
    @Expose
    private Repository repository;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
