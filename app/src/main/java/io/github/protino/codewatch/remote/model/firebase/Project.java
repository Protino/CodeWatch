package io.github.protino.codewatch.remote.model.firebase;

import java.util.List;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Project {

    private String id;
    private String name;
    private String publicUrl;
    private List<Integer> timeSpent;
    private List<CustomPair> languageList;
    private List<CustomPair> osPairList;
    private List<CustomPair> editorPaiList;

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

    public List<CustomPair> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<CustomPair> languageList) {
        this.languageList = languageList;
    }

    public List<CustomPair> getOsPairList() {
        return osPairList;
    }

    public void setOsPairList(List<CustomPair> osPairList) {
        this.osPairList = osPairList;
    }

    public List<CustomPair> getEditorPaiList() {
        return editorPaiList;
    }

    public void setEditorPaiList(List<CustomPair> editorPaiList) {
        this.editorPaiList = editorPaiList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
