package io.github.protino.codewatch.model.firebase;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class CustomPair {
    private String name;
    private Integer totalSeconds;

    public CustomPair() {
    }

    public CustomPair(String name, Integer totalSeconds) {
        this.name = name;
        this.totalSeconds = totalSeconds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
