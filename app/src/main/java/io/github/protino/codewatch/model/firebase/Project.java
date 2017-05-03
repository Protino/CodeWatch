package io.github.protino.codewatch.model.firebase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Project implements Serializable {

    private String id;
    private String name;
    private String publicUrl;
    private List<Integer> timeSpent; // ending with user's current date, max is 7 days
    private Map<String, Integer> languageList;
    private Map<String, Integer> osPairList;
    private Map<String, Integer> editorPaiList;

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public List<Integer> getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(List<Integer> timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Map<String, Integer> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(Map<String, Integer> languageList) {
        this.languageList = languageList;
    }

    public Map<String, Integer> getOsPairList() {
        return osPairList;
    }

    public void setOsPairList(Map<String, Integer> osPairList) {
        this.osPairList = osPairList;
    }

    public Map<String, Integer> getEditorPaiList() {
        return editorPaiList;
    }

    public void setEditorPaiList(Map<String, Integer> editorPaiList) {
        this.editorPaiList = editorPaiList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
