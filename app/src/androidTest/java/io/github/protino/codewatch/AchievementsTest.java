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
