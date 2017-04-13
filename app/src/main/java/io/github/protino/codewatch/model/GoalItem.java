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

    private String uid; //for deletion purposes

    private String name;
    @GoalType
    private int type;
    private long data; //depends on type
    private long extraData;

    public GoalItem() {
        //needed by firebase
    }

    public GoalItem(String uid, String name, int type, long data) {
        this.uid = uid;
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "Id - " + name;
    }

    public long getExtraData() {
        return extraData;
    }

    public void setExtraData(long extraData) {
        this.extraData = extraData;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LANGUAGE_GOAL, PROJECT_DEADLINE_GOAL, PROJECT_DAILY_GOAL})
    public @interface GoalType {
    }
}
