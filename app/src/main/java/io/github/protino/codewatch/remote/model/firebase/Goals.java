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

import java.util.Map;

/**
 * @author Gurupad Mamadapur
 */

public class Goals {
    private Map<String, ProjectGoal> projectGoals;
    private Map<String, Integer> languageGoals;

    public Goals() {
    }

    public Map<String, ProjectGoal> getProjectGoals() {
        return projectGoals;
    }

    public void setProjectGoals(Map<String, ProjectGoal> projectGoals) {
        this.projectGoals = projectGoals;
    }

    public Map<String, Integer> getLanguageGoals() {
        return languageGoals;
    }

    public void setLanguageGoals(Map<String, Integer> languageGoals) {
        this.languageGoals = languageGoals;
    }
}
