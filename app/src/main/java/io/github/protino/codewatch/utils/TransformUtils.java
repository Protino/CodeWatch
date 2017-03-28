package io.github.protino.codewatch.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.protino.codewatch.model.WakatimeData;
import io.github.protino.codewatch.model.firebase.Project;
import io.github.protino.codewatch.model.firebase.Stats;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.model.project.ProjectsData;
import io.github.protino.codewatch.model.statistics.Editor;
import io.github.protino.codewatch.model.statistics.Language;
import io.github.protino.codewatch.model.statistics.OperatingSystem;
import io.github.protino.codewatch.model.statistics.StatsData;
import io.github.protino.codewatch.model.user.ProfileData;

/**
 * Transform {@link io.github.protino.codewatch.model.WakatimeData} into
 * {@link io.github.protino.codewatch.model.firebase.User}.
 */
public class TransformUtils {
    private final WakatimeData wakatimeData;
    private User user;

    public TransformUtils(WakatimeData wakatimeData, User user) {
        this.wakatimeData = wakatimeData;
        this.user = user;
    }

    public User execute() {
        transformProfileData();
        transformStats();
        transformProjectsData();
        return user;
    }

    private User transformProjectsData() {
        Map<String, Project> projectMap = new HashMap<>();
        Project project;

        List<ProjectsData> dataList = wakatimeData.getProjectsResponse().getProjectsList();
        for (ProjectsData data : dataList) {
            project = new Project();
            project.setTimeSpent(null);
            project.setEditorPaiList(null);
            project.setLanguageList(null);
            project.setOsPairList(null);

            project.setPublicUrl(data.getPublicUrl());
            project.setName(data.getName());
            project.setId(data.getId());
            projectMap.put(data.getId(), project);
        }

        user.setProjects(projectMap);
        return user;
    }

    private User transformStats() {
        Stats stats = new Stats();
        StatsData statsData = wakatimeData.getStatsResponse().getStatsData();
        stats.setUpToDate(statsData.getIsUpToDate());
        stats.setStartDate(statsData.getStart());
        stats.setEndDate(statsData.getEnd());
        stats.setBestDayDate(statsData.getBestDay().getDate());
        stats.setBestDaySeconds(statsData.getBestDay().getTotalSeconds());
        stats.setDailyAverageSeconds(statsData.getDailyAverage());
        stats.setTotalSeconds(statsData.getTotalSeconds());
        stats.setChangeInTotalSeconds(wakatimeData.getChangeInTotalSeconds());

        stats.setProjectPairList(wakatimeData.getProjectStatsList());

        Map<String, Integer> map = new HashMap<>();

        for (Language language : statsData.getLanguages()) {
            map.put(language.getName(), language.getTotalSeconds());
        }
        stats.setLanguagesMap(map);

        map = new HashMap<>();
        for (OperatingSystem operatingSystem : statsData.getOperatingSystems()) {
            map.put(operatingSystem.getName(), operatingSystem.getTotalSeconds());
        }
        stats.setOsMap(map);

        map = new HashMap<>();

        for (Editor editor : statsData.getEditors()) {
            map.put(editor.getName(), editor.getTotalSeconds());
        }
        stats.setEditorsMap(map);

        user.setStats(stats);
        return user;
    }

    public User transformProfileData() {
        ProfileData profileData = wakatimeData.getUserResponse().getProfileData();
        user.setEmail(profileData.getEmail());
        user.setDisplayName(profileData.getDisplayName());
        user.setAchievements(0); // TODO: 16-03-2017 Store as achievements as auth data
        user.setCurrentPlan(profileData.getPlan());
        user.setIsEmailConfirmed(profileData.getIsEmailConfirmed());
        user.setUserId(profileData.getId());
        user.setWebsite(profileData.getWebsite());
        user.setTimeZone(profileData.getTimezone());
        user.setPhotoUrl(profileData.getPhoto());
        user.setHasPremiumFeatures(profileData.getHasPremiumFeatures());
        // Todo : Add generic language goals here
        return user;
    }
}
