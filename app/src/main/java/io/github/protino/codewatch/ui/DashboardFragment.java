package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.PieChartItem;
import io.github.protino.codewatch.ui.adapter.StatsAdapter;
import io.github.protino.codewatch.ui.widget.PerformanceBarView;
import io.github.protino.codewatch.utils.FormatUtils;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class DashboardFragment extends Fragment {

    private static final int LANGUAGE_CHART_ID = 1;
    private static final int ACTIVITY_CHART_ID = 2;
    private static final int OS_CHART_ID = 3;
    private static final int EDITORS_CHART_ID = 4;

    //@formatter:off
    //activity
    @BindView(R.id.linechart_activity) public LineChart lineChart;
    @BindView(R.id.activity_total)public TextView todaysLogTimeText;
    //performance
    @BindView(R.id.performance_bar)public PerformanceBarView performanceBarView;
    @BindView(R.id.daily_average_text)public TextView dailyAverageText;
    @BindView(R.id.change_in_daily_average_text) public TextView percentChangeText;
    @BindView(R.id.today_log_percent_text) public TextView todaysLogPercentText;
    //languages
    @BindView(R.id.piechart_languages) public PieChart pieChartLanguages;
    @BindView(R.id.expand_piechart_language)public ImageView expandLanguages;
    @BindView(R.id.list_languages) public RecyclerView languagesListview;
    //editors
    @BindView(R.id.piechart_editors)public PieChart pieChartEditors;
    @BindView(R.id.expand_piechart_editors) public ImageView expandEditors;
    @BindView(R.id.list_editors) public RecyclerView editorsListView;
    //os
    @BindView(R.id.piechart_os) public PieChart pieChartOs;
    @BindView(R.id.expand_piechart_os) public ImageView expandOs;
    @BindView(R.id.list_os) public RecyclerView osListview;

    @BindArray(R.array.chart_colors) public int[] chartColors;
    //@formatter:on
    private SparseBooleanArray isExpandedMap = new SparseBooleanArray();
    private List<PieChartItem> languageDataItems = new ArrayList<>();
    private List<PieChartItem> editorDataItems = new ArrayList<>();
    private List<PieChartItem> osDataItems = new ArrayList<>();

    private StatsAdapter editorsListAdapter;
    private StatsAdapter languageListAdapter;
    private StatsAdapter osListAdapter;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        setUpActivityChart();
        setUpPerformanceBar();
        setUpLanguagesChart();
        setUpEditorsChart();
        setUpOsChart();
        setListeners();
        return rootView;
    }

    private void setListeners() {
        pieChartLanguages.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(LANGUAGE_CHART_ID));
        pieChartEditors.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(EDITORS_CHART_ID));
        pieChartOs.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(OS_CHART_ID));
    }

    private void setUpPerformanceBar() {
        //dummy data
        final float todaysTotalSeconds = 5 * 60 * 60f + 60 + 67;
        final float currentDailyAverage = 8 * 60 * 60f + 60 + 60 + 45;
        final float previousDailyAverage = 4 * 60 * 60f + 4561;
        String result = context.getString(R.string.result_increase);
        float changeInAveragePercent = ((currentDailyAverage - previousDailyAverage) / previousDailyAverage) * 100f;
        if (changeInAveragePercent < 0) {
            changeInAveragePercent *= -1;
            result = getString(R.string.result_decreased);
        }
        final float todayLogPercent = (todaysTotalSeconds / currentDailyAverage) * 100f;

        performanceBarView.setGoal(currentDailyAverage);
        performanceBarView.setProgress(todaysTotalSeconds);

        percentChangeText.setText(context.getString(R.string.percent_change_in_daily_avg,
                changeInAveragePercent, result));


        dailyAverageText.setText(context.getString(R.string.daily_average_format,
                FormatUtils.getFormattedTime(context, (int) currentDailyAverage)));

        todaysLogPercentText.setText(context.getString(R.string.todays_log_percent_text, todayLogPercent));

        todaysLogTimeText.setText(context.getString(R.string.todays_total_log_time,
                FormatUtils.getFormattedTime(context, (int) todaysTotalSeconds)));
    }

    private void setUpActivityChart() {

        LineData data = generateActivityLineData();
        data.setHighlightEnabled(false);

        Legend l = lineChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setFormSize(12f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(8f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(8f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value == 0 ? "" : String.valueOf(value);
            }
        });
        leftAxis.setGranularity(1f);
        leftAxis.setAxisLineWidth(2f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);
        xAxis.setAxisLineWidth(2f);


        lineChart.getDescription().setEnabled(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDragDecelerationEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setData(data);
        lineChart.animateX(1500, Easing.EasingOption.Linear);
    }

    private LineData generateActivityLineData() {

        Random random = new Random();
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        ArrayList<Entry> entries3 = new ArrayList<>();

        for (int index = 0; index < 7; index++) {
            entries.add(new Entry(index, random.nextInt(20)));
            entries2.add(new Entry(index, random.nextInt(20)));
            entries3.add(new Entry(index, random.nextInt(20)));
        }
        entries.remove(4);
        entries.remove(4);
        entries.remove(4);


        LineDataSet set = new LineDataSet(entries, "Go");
        formatLineDataSet(set, chartColors[0], 3);

        LineDataSet set2 = new LineDataSet(entries2, "Wac");
        formatLineDataSet(set2, chartColors[1], 3);

        LineDataSet set3 = new LineDataSet(entries3, "AWD");
        formatLineDataSet(set3, chartColors[2], 3);
        d.addDataSet(set);
        d.addDataSet(set2);
        d.addDataSet(set3);
        return d;
    }

    private void formatLineDataSet(LineDataSet set, int color, int width) {
        set.setDrawValues(false);
        set.setDrawHighlightIndicators(false);
        set.setMode(LineDataSet.Mode.LINEAR);

        set.setColor(color);
        set.setLineWidth(width);
        set.setCircleColor(color);
        set.setCircleRadius(width + 2);
    }

    private void setUpOsChart() {
        formatPieChart(pieChartOs);
        formatPieChartLegend(pieChartOs.getLegend());
        setUpData(osDataItems);
        pieChartOs.setData(generatePieData(osDataItems));
        pieChartOs.highlightValues(null);
        pieChartOs.animateXY(1500, 1500);

        osListview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        osListAdapter = new StatsAdapter(context, languageDataItems, "");
        osListview.setAdapter(osListAdapter);
    }

    private void setUpEditorsChart() {
        formatPieChart(pieChartEditors);
        formatPieChartLegend(pieChartEditors.getLegend());
        setUpData(editorDataItems);
        pieChartEditors.setData(generatePieData(editorDataItems));
        pieChartEditors.highlightValues(null);
        pieChartEditors.animateXY(1500, 1500);

        editorsListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        editorsListAdapter = new StatsAdapter(context, editorDataItems, "");
        editorsListView.setAdapter(editorsListAdapter);
    }

    private void setUpLanguagesChart() {
        formatPieChart(pieChartLanguages);
        formatPieChartLegend(pieChartLanguages.getLegend());
        setUpData(languageDataItems);
        pieChartLanguages.setData(generatePieData(languageDataItems));
        pieChartLanguages.highlightValues(null);
        pieChartLanguages.animateXY(1500, 1500);
        languagesListview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        languageListAdapter = new StatsAdapter(context, languageDataItems, "");
        languagesListview.setAdapter(languageListAdapter);
    }

    private void setUpData(List<PieChartItem> itemList) {
        String[] labels = new String[]{"CodeWatch", "Sunshine", "Stock Hawk", "Hacker", "Docker", "Chalk", "Lego", "Thirsty"};
        int[] time = new int[]{513221, 65412, 3512, 44561, 10005, 21511, 2333, 888888};
        float[] percent = calculatePercents(time);

        PieChartItem item;
        for (int i = 0; i < labels.length; i++) {
            item = new PieChartItem();
            item.setName(labels[i]);
            item.setPercent(percent[i]);
            item.setTime(time[i]);
            itemList.add(i, item);
        }
    }

    private float[] calculatePercents(int[] list) {
        float totalSum = 0;
        float[] percents = new float[list.length];
        for (int item : list) {
            totalSum += item;
        }
        for (int i = 0; i < list.length; i++) {
            percents[i] = (list[i] / totalSum) * 100;
        }
        return percents;
    }

    private PieData generatePieData(List<PieChartItem> pieChartItems) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (PieChartItem item : pieChartItems) {
            entries.add(new PieEntry(item.getTime(), item.getName(), item.getPercent()));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setAutomaticallyDisableSliceSpacing(true);

        pieDataSet.setColors(chartColors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new CustomPercentFormatter());
        pieData.setValueTextColor(Color.WHITE);
        return pieData;
    }

    private void formatPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setDrawEntryLabels(false);
    }

    private void formatPieChartLegend(Legend legend) {
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(false);
    }

    @OnClick({R.id.expand_piechart_editors, R.id.expand_piechart_language, R.id.expand_piechart_os})
    public void onExpand(View view) {
        boolean isExpanded = isExpandedMap.get(view.getId());
        Drawable drawable = ContextCompat.getDrawable(getActivity(),
                isExpanded ? R.drawable.ic_expand_more_black_24dp : R.drawable.ic_expand_less_black_24dp);
        isExpandedMap.put(view.getId(), !isExpanded);
        switch (view.getId()) {
            case R.id.expand_piechart_language:
                expandLanguages.setImageDrawable(drawable);
                handleHighlights(pieChartLanguages, isExpanded);
                toggleListViewVisibility(!isExpanded, languagesListview, languageListAdapter);
                break;
            case R.id.expand_piechart_editors:
                expandEditors.setImageDrawable(drawable);
                handleHighlights(pieChartEditors, isExpanded);
                toggleListViewVisibility(!isExpanded, editorsListView, editorsListAdapter);
                break;
            case R.id.expand_piechart_os:
                expandOs.setImageDrawable(drawable);
                handleHighlights(pieChartEditors, isExpanded);
                toggleListViewVisibility(!isExpanded, osListview, osListAdapter);
                break;
            default://ignore
                break;
        }
    }

    private void toggleListViewVisibility(boolean expand, RecyclerView view, StatsAdapter adapter) {
        view.setVisibility(expand ? View.VISIBLE : View.GONE);
        if (!expand) {
            view.setAdapter(new StatsAdapter(context, adapter.getItemList(), ""));
        }
    }

    private void handleHighlights(PieChart pieChart, boolean isExpanded) {
        if (isExpanded) {
            Highlight[] highlights = pieChart.getHighlighted();
            if (highlights != null && highlights.length != 0) {
                pieChart.highlightValues(null);
            }
        }
    }

    /*  notifyDataSetChanged didn't work. Somehow there were duplicate items selected.
        Hence resetting the adapter.
        todo : Find a better way to reflect changes in the adapter
     */
    private void resetAdapter(RecyclerView view, StatsAdapter adapter, String label) {
        adapter = new StatsAdapter(context, adapter.getItemList(), label);
        view.setAdapter(adapter);
    }

    private class CustomPercentFormatter extends PercentFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value > 3) {
                return super.getFormattedValue(value, axis);
            } else {
                return "";
            }
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (value > 3) {
                return super.getFormattedValue(value, entry, dataSetIndex, viewPortHandler);
            } else {
                return "";
            }
        }
    }

    private class CustomOnValueSelectedListener implements OnChartValueSelectedListener {

        private int CHART_ID;

        private CustomOnValueSelectedListener() {
        }

        private CustomOnValueSelectedListener(int chart_id) {
            CHART_ID = chart_id;
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Timber.d(e.toString());
            switch (CHART_ID) {
                case LANGUAGE_CHART_ID:
                    if (!isExpandedMap.get(expandLanguages.getId())) {
                        onExpand(expandLanguages);
                    }
                    resetAdapter(languagesListview, languageListAdapter, ((PieEntry) e).getLabel());
                    break;
                case OS_CHART_ID:
                    if (!isExpandedMap.get(expandOs.getId())) {
                        onExpand(expandOs);
                    }
                    resetAdapter(osListview, osListAdapter, ((PieEntry) e).getLabel());
                    break;
                case EDITORS_CHART_ID:
                    if (!isExpandedMap.get(expandEditors.getId())) {
                        onExpand(expandEditors);
                    }
                    resetAdapter(editorsListView, editorsListAdapter, ((PieEntry) e).getLabel());
                    break;
                default: //common code
                    break;
            }
        }

        @Override
        public void onNothingSelected() {
            switch (CHART_ID) {
                case LANGUAGE_CHART_ID:
                    onExpand(expandLanguages);
                    break;
                case OS_CHART_ID:
                    onExpand(expandOs);
                    break;
                case EDITORS_CHART_ID:
                    onExpand(expandEditors);
                    break;
                default:
                    break;
            }
        }
    }
}
