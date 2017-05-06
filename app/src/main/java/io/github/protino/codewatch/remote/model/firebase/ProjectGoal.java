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

package io.github.protino.codewatch.remote.model.firebase;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectGoal {
    /**
     * time since epoch in seoncds
     */
    private int deadline; //time since epoch in seconds
    /**
     * Number of seconds that must be spent on the project daily,
     */
    private int daily;

    public ProjectGoal() {
        //set goals to -1 to imply no goals are set by the user
        deadline = -1;
        daily = -1;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getDaily() {
        return daily;
    }

    public void setDaily(int daily) {
        this.daily = daily;
    }
}
