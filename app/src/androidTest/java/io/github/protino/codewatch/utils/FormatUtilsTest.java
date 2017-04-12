package io.github.protino.codewatch.utils;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Gurupad Mamadapur
 */
public class FormatUtilsTest {

    private static final int TEST_SECONDS = (60 * 60 * 68) + (60 * 45); // 68 hours 45 min
    private static final String EXPECTED_RESULT = "68 h 45 m";

    @Test
    public void getFormattedTime() throws Exception {
        String actualResult = FormatUtils.getFormattedTime(InstrumentationRegistry.getTargetContext(), TEST_SECONDS);
        assertEquals(EXPECTED_RESULT, actualResult);
    }

    @Test
    public void getHoursAndMinutes() throws Exception {
        assertEquals(68, FormatUtils.getHoursAndMinutes(TEST_SECONDS).first.intValue());
        assertEquals(45, FormatUtils.getHoursAndMinutes(TEST_SECONDS).second.intValue());
    }
}