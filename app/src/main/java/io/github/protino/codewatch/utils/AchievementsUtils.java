package io.github.protino.codewatch.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.protino.codewatch.model.BadgeItem;

import static io.github.protino.codewatch.utils.Constants.ACHIEVEMENTS_MAP;
import static io.github.protino.codewatch.utils.Constants.ARDENT;
import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGES;
import static io.github.protino.codewatch.utils.Constants.COMPETITOR;
import static io.github.protino.codewatch.utils.Constants.DEVOTED;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGES;
import static io.github.protino.codewatch.utils.Constants.HARD_WORKING;
import static io.github.protino.codewatch.utils.Constants.INSOMNIAC;
import static io.github.protino.codewatch.utils.Constants.LEADER;
import static io.github.protino.codewatch.utils.Constants.LOYAL;
import static io.github.protino.codewatch.utils.Constants.MASTER;
import static io.github.protino.codewatch.utils.Constants.SANE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGES;

/**
 * @author Gurupad Mamadapur
 */

public class AchievementsUtils {

    /**
     * @param achievements long data representing set and unset badges
     * @return list of resource id of badges that are unlocked
     */
    public static List<Integer> getUnlockedAchievements(long achievements) {
        List<Integer> array = new ArrayList<>();
        for (int i = 0; i < ACHIEVEMENTS_MAP.size(); i++) {
            int id = ACHIEVEMENTS_MAP.keyAt(i);
            int position = ACHIEVEMENTS_MAP.get(id);

            if ((achievements & (1L << position)) != 0) {
                array.add(id);
            }
        }
        return array;
    }

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("ResourceType")
    public static Map<Integer, Pair<String, String>> obtainBadgeMap(Context context, @ArrayRes int id) {

        TypedArray badgeArray = context.getResources().obtainTypedArray(id);

        Map<Integer, Pair<String, String>> badgeMap = new HashMap<>();
        for (int i = 0; i < badgeArray.length(); i++) {
            int resId = badgeArray.getResourceId(i, -1);
            if (resId != -1) {
                TypedArray array = context.getResources().obtainTypedArray(resId);
                badgeMap.put(resId, new Pair<>(array.getString(0), array.getString(1)));
                array.recycle();
            }
        }
        badgeArray.recycle();
        return badgeMap;
    }

    public static List<BadgeItem> createBadgeItems(
            Map<Integer, Pair<String, String>> badgeMap, List<Integer> list, @BadgeItem.BadgeType int badgeType) {

        List<BadgeItem> badgeItems = new ArrayList<>();
        for (Map.Entry<Integer, Pair<String, String>> entry : badgeMap.entrySet()) {
            BadgeItem badgeItem = new BadgeItem(badgeType);
            badgeItem.setName(entry.getValue().first);
            badgeItem.setRequirement(entry.getValue().second);
            badgeItem.setIsUnlocked(list.contains(entry.getKey()));
            badgeItems.add(badgeItem);
        }
        return badgeItems;
    }

    public static int getBadgeCount(long currentAchievements, @BadgeItem.BadgeType int badgeType) {
        int count = 0;
        int[] positions;
        switch (badgeType) {
            case GOLD_BADGE:
                positions = GOLD_BADGES;
                break;
            case SILVER_BADGE:
                positions = SILVER_BADGES;
                break;
            case BRONZE_BADGE:
                positions = BRONZE_BADGES;
                break;
            default:
                throw new UnsupportedOperationException("Invalid badge");
        }
        for (int i = 0; i < positions.length; i++) {
            if ((currentAchievements & (1L << positions[i])) != 0) {
                count++;
            }
        }
        return count;
    }

    public static long checkAchievements(int rank, int dailyAverage) {
        List<Integer> positions = new ArrayList<>();

        //First check achievements related to rank
        if (rank <= 10) {
            positions.add(LEADER);
        }
        if (rank <= 50) {
            positions.add(MASTER);
        }
        if (rank <= 100) {
            positions.add(COMPETITOR);
        }

        //Now, related to daily average
        //Let's add a tolerance of 15 min
        dailyAverage += (15 * 60);
        int hours = (int) TimeUnit.SECONDS.toHours(dailyAverage);
        if (hours >= 18) {
            positions.add(INSOMNIAC);
            positions.add(HARD_WORKING);
            positions.add(SANE);
        } else if (hours >= 10) {
            positions.add(HARD_WORKING);
            positions.add(SANE);
        } else if (hours >= 7) {
            positions.add(SANE);
        }

        //now set all those bits
        long newAchievements = 0;
        for (Integer bitPosition : positions) {
            newAchievements |= (1L << bitPosition);
        }
        return newAchievements;
    }

    public static long checkUsageAchievements(int consecutiveDays) {
        List<Integer> positions = new ArrayList<>();
        long newAchievements = 0;
        if (consecutiveDays == 30) {
            positions.add(DEVOTED);
        } else if (consecutiveDays == 14) {
            positions.add(ARDENT);
        } else if (consecutiveDays == 7) {
            positions.add(LOYAL);
        }
        for (Integer bitPosition : positions) {
            newAchievements |= (1L << bitPosition);
        }
        return newAchievements;
    }
}
