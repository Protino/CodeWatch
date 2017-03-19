package io.github.protino.codewatch.utils;

import java.util.ArrayList;
import java.util.List;

import io.github.protino.codewatch.remote.model.WakatimeData;
import io.github.protino.codewatch.remote.model.firebase.CustomPair;
import io.github.protino.codewatch.remote.model.firebase.Stats;
import io.github.protino.codewatch.remote.model.firebase.User;
import io.github.protino.codewatch.remote.model.statistics.Editor;
import io.github.protino.codewatch.remote.model.statistics.Language;
import io.github.protino.codewatch.remote.model.statistics.OperatingSystem;
import io.github.protino.codewatch.remote.model.statistics.StatsData;
import io.github.protino.codewatch.remote.model.user.ProfileData;

/**
 * Transform {@link io.github.protino.codewatch.remote.model.WakatimeData} into
 * {@link io.github.protino.codewatch.remote.model.firebase.User}.
 */
public class Transform {
    private final WakatimeData wakatimeData;
    private User user;

    public Transform(WakatimeData wakatimeData, User user) {
        this.wakatimeData = wakatimeData;
        this.user = user;
    }

    public User execute() {
        transformProfileData();
        transformStats();
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

        List<CustomPair> languagePairsList = new ArrayList<>();
        List<CustomPair> osPairList = new ArrayList<>();
        List<CustomPair> editorPairList = new ArrayList<>();

        for (Language language : statsData.getLanguages()) {
            languagePairsList.add(new CustomPair(language.getName(), language.getTotalSeconds()));
        }
        for (OperatingSystem operatingSystem : statsData.getOperatingSystems()) {
            osPairList.add(new CustomPair(operatingSystem.getName(), operatingSystem.getTotalSeconds()));
        }
        for (Editor editor : statsData.getEditors()) {
            editorPairList.add(new CustomPair(editor.getName(), editor.getTotalSeconds()));
        }
        stats.setLanguagePairList(languagePairsList);
        stats.setOsPairList(osPairList);
        stats.setEditorPairList(editorPairList);

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
