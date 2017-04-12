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

import io.github.protino.codewatch.model.BadgeItem;

import static io.github.protino.codewatch.utils.Constants.ACHIEVEMENTS_MAP;
import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGES;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGES;
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
}
