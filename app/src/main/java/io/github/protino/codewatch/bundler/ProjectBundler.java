package io.github.protino.codewatch.bundler;

import android.os.Bundle;

import icepick.Bundler;
import io.github.protino.codewatch.model.firebase.Project;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectBundler implements Bundler<Project> {
    @Override
    public void put(String s, Project project, Bundle bundle) {
        bundle.putSerializable(s, project);
    }

    @Override
    public Project get(String s, Bundle bundle) {
        return (Project) bundle.getSerializable(s);
    }
}
