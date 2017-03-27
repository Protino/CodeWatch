package io.github.protino.codewatch.utils;

import android.content.Context;
import android.util.Pair;

import io.github.protino.codewatch.R;

/**
 * @author Gurupad Mamadapur
 */

public class FormatUtils {


    public static String getFormattedTime(Context context, int seconds) {
        Pair<Integer, Integer> pair = getHoursAndMinutes(seconds);
        int hours = pair.first;
        int minutes = pair.second;
        return context.getString(R.string.default_time_format, hours, minutes);
    }

    public static Pair<Integer, Integer> getHoursAndMinutes(int seconds) {
        return new Pair<>(
                (seconds / (60 * 60)) % 24,
                (seconds / 60) % 60);
    }
}
