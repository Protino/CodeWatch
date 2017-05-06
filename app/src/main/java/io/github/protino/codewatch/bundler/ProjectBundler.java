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

package io.github.protino.codewatch.bundler;

import android.os.Bundle;

import icepick.Bundler;
import io.github.protino.codewatch.model.firebase.Project;

/**
 * Custom bundler to preserve {@link Project} object
 *
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
