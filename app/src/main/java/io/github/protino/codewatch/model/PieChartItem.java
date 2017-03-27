package io.github.protino.codewatch.model;

/**
 * @author Gurupad Mamadapur
 */

public class PieChartItem {
    private String name;
    private int time;
    private float percent;

    public PieChartItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
