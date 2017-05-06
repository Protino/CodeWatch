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

    public BadgeItem(@BadgeType int type) {
        this.type = type;
    }

    public BadgeItem(int type, String name, String requirement) {
        this.type = type;
        this.name = name;
        this.requirement = requirement;
    }

    public @BadgeType int getType() {
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
    public @interface BadgeType {}
}
