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
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.protino.codewatch.ui.LoginActivity;
import io.github.protino.codewatch.utils.CacheUtils;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> testRule = new ActivityTestRule<>(
            LoginActivity.class, true, false);

    @Test
    public void testLogin() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        CacheUtils.clearLoginInfo(context);
        testRule.launchActivity(new Intent());
        Thread.sleep(18000);
        assertTrue(CacheUtils.isLoggedIn(context));
    }

}
