package io.github.protino.codewatch.utils;

import android.content.Context;
import android.util.Pair;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.protino.codewatch.R;

/**
 * @author Gurupad Mamadapur
 */

public class FormatUtils {


    public static String getFormattedTime(Context context, int seconds) {
        Pair<Integer, Integer> pair = getHoursAndMinutes(seconds);
        int hours = pair.first;
        int minutes = pair.second;
        if (hours == 0 && minutes == 0) {
            return context.getString(R.string.seconds, seconds);
        } else if (hours == 0) {
            return context.getString(R.string.minutes, minutes);
        } else if (minutes == 0) {
            return context.getString(R.string.hours, hours);
        } else {
            return context.getString(R.string.default_time_format, hours, minutes);
        }
    }

    public static Pair<Integer, Integer> getHoursAndMinutes(int seconds) {
        return new Pair<>(
                (seconds / (60 * 60)),
                (seconds / 60) % 60);
    }

    public static String getDeadlineGoalText(Context context, String projectName, long deadline) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String date = simpleDateFormat.format(new Date(deadline));
        return context.getString(R.string.project_deadline_goal, projectName, date);
    }

    public static String getRemainingDaysText(Context context, int days) {
        return (days > 0)
                ? context.getString(R.string.remaining_days, days, context.getString((days == 1) ? R.string.day : R.string.day))
                : context.getString(R.string.deadline_reached);
    }

    public BarXAxisValueFormatter getBarXAxisValueFormatterInstance(long referenceTime) {
        return new BarXAxisValueFormatter(referenceTime);
    }

    public class BarXAxisValueFormatter implements IAxisValueFormatter {

        private final DateTimeFormatter dateFormat;
        private final DateTime date;

        public BarXAxisValueFormatter(long referenceTime) {
            dateFormat = DateTimeFormat.forPattern("d");
            date = new DateTime(referenceTime);
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return dateFormat.print(date.plusDays((int) value));
        }
    }
}