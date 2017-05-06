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

package io.github.protino.codewatch.model.firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gurupad Mamadapur
 */

public class LanguageGoal {
    public String goalId;
    /**
     * Number of hours.
     */
    private int dailyGoal;
    private List<Integer> progressSoFar; //time in seconds

    public LanguageGoal() {
        dailyGoal = -1;
        progressSoFar = new ArrayList<>();
    }

    public LanguageGoal(String goalId, int dailyGoal, List<Integer> progressSoFar) {
        this.goalId = goalId;
        this.dailyGoal = dailyGoal;
        this.progressSoFar = progressSoFar;
    }

    public List<Integer> getProgressSoFar() {
        return progressSoFar;
    }

    public void setProgressSoFar(List<Integer> progressSoFar) {
        this.progressSoFar = progressSoFar;
    }

    public int getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(int dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }
}
