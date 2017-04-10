package io.github.protino.codewatch.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGE;

/**
 * @author Gurupad Mamadapur
 */

public class BadgeItem {

    @BadgeType
    private int type;
    private String name;
    private String requirement;
    private boolean isUnlocked;

    public BadgeItem(int type) {
        this.type = type;
    }

    public BadgeItem(int type, String name, String requirement) {
        this.type = type;
        this.name = name;
        this.requirement = requirement;
    }

    public int getType() {
        return type;
    }

    public void setType(@BadgeType int type) {
        this.type = type;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GOLD_BADGE, SILVER_BADGE, BRONZE_BADGE})
    @interface BadgeType {
    }
}
