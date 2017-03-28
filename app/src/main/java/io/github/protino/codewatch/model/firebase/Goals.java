package io.github.protino.codewatch.model.firebase;

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
