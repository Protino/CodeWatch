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

import java.util.List;

/**
 * @author Gurupad Mamadapur
 */
public class ProfileItem {

    private String name;
    private String photoUrl;
    private int dailyAverage;
    private int rank;

    private String website;
    private String email;
    private String location;
    private String languageStats;

    private List<Integer> unlockedAchievements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getDailyAverage() {
        return dailyAverage;
    }

    public void setDailyAverage(int dailyAverage) {
        this.dailyAverage = dailyAverage;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguageStats() {
        return languageStats;
    }

    public void setLanguageStats(String languageStats) {
        this.languageStats = languageStats;
    }

    public List<Integer> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(List<Integer> unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
