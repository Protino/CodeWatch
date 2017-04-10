package io.github.protino.codewatch;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

@RunWith(AndroidJUnit4.class)
public class AchievementsTest {

    @SuppressWarnings("ResourceType")
    @Test
    public void testBadgesArray() {
        Context context = InstrumentationRegistry.getTargetContext();

        TypedArray goldBadges = context.getResources().obtainTypedArray(R.array.gold_badges);

        Map<String, String> badgeMap = new HashMap<>();
        for (int i = 0; i < goldBadges.length(); i++) {
            int resId = goldBadges.getResourceId(i, -1);
            if (resId != -1) {
                TypedArray array = context.getResources().obtainTypedArray(resId);
                badgeMap.put(array.getString(0), array.getString(1));
                array.recycle();
            }
        }
        Timber.d(badgeMap.toString());
        Assert.assertNotNull(badgeMap.get("Leader"));
        goldBadges.recycle();
    }

    @Test
    public void testUnlockedInt() {
        int tempUnlockedInt = 0;

    }
}
