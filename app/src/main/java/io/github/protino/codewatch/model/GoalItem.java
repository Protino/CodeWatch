package io.github.protino.codewatch.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class GoalItem {
    private String id;
    @GoalType private int type;
    private long data; //depends on type

    public GoalItem(String id, int type, long data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(@GoalType int type) {
        this.type = type;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Type - " + type +
                "Data - " + data +
                "Id - " + id;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LANGUAGE_GOAL, PROJECT_DEADLINE_GOAL, PROJECT_DAILY_GOAL})
    @interface GoalType {
    }
}
