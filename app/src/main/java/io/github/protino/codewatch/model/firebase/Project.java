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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class Project implements Serializable {

    public String id;
    public String name;
    public String publicUrl;
    public List<Integer> timeSpent; // ending with user's current date, max is 7 days
    public Map<String, Integer> languageList;
    public Map<String, Integer> osPairList;
    public Map<String, Integer> editorPaiList;

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public List<Integer> getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(List<Integer> timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Map<String, Integer> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(Map<String, Integer> languageList) {
        this.languageList = languageList;
    }

    public Map<String, Integer> getOsPairList() {
        return osPairList;
    }

    public void setOsPairList(Map<String, Integer> osPairList) {
        this.osPairList = osPairList;
    }

    public Map<String, Integer> getEditorPaiList() {
        return editorPaiList;
    }

    public void setEditorPaiList(Map<String, Integer> editorPaiList) {
        this.editorPaiList = editorPaiList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
