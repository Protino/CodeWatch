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
 * Helper class to format data values displayed in charts
 *
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
