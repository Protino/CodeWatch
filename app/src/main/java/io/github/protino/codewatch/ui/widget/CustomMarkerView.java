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

package io.github.protino.codewatch.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.utils.FormatUtils;
import timber.log.Timber;

/**
 * A pop up view that is displayed when a data item on a chart is clicked, thus revealing
 * further data
 *
 * @author Gurupad Mamadapur
 */

@SuppressLint("ViewConstructor")
public class CustomMarkerView extends MarkerView {

    private final Context context;
    private final DateTime date;
    private final DateTimeFormatter dateFormat;
    private final TextView textView;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context        this is needed to fetch string resources.
     * @param layoutResource the layout resource to use for the MarkerView.
     * @param referenceTime  additive value to each data value.
     */
    public CustomMarkerView(Context context, int layoutResource, long referenceTime) {
        super(context, layoutResource);
        this.context = context;
        textView = (TextView) findViewById(R.id.marker_text);

        dateFormat = DateTimeFormat.forPattern("d MMM");
        date = new DateTime(referenceTime);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float seconds = e.getY();
        String formattedTime = FormatUtils.getFormattedTime(context, (int) Math.ceil(seconds));
        DateTime nextDate = date.plusDays((int) e.getX());
        textView.setText(context.getString(R.string.marker_text, formattedTime, dateFormat.print(nextDate)));
        Timber.d(formattedTime);
        super.refreshContent(e, highlight);
    }

    /* Offsetting to place the view center-horizontally */
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
