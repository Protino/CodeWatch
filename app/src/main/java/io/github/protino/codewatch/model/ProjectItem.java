package io.github.protino.codewatch.model;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectItem {
    private String id; //irrelevant
    private String name;
    private int totalSeconds;

    public ProjectItem() {

    }

    public ProjectItem(String id, String name, int totalSeconds) {
        this.id = id;
        this.name = name;
        this.totalSeconds = totalSeconds;
    }

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

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
