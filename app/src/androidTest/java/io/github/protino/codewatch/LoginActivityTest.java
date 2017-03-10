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
import io.github.protino.codewatch.utils.Cache;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> testRule = new ActivityTestRule<>(
            LoginActivity.class, true, false);

    @Test
    public void testLogin() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        Cache.clearLoginInfo(context);
        testRule.launchActivity(new Intent());
        Thread.sleep(15000);
        assertTrue(Cache.isLoggedIn(context));
    }

}
