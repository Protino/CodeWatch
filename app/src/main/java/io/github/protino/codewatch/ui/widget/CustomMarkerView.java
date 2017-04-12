package io.github.protino.codewatch.ui.widget;

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
 * @author Gurupad Mamadapur
 */

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

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
