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

import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class GoalItem {

    public String uid; //for deletion purposes
    public String name;

    @GoalType
    public int type;
    public long data; //depends on type
    public long extraData;

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
