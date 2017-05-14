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

package io.github.protino.codewatch.remote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.WakatimeDataWrapper;
import io.github.protino.codewatch.model.firebase.Stats;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.ui.ChartFragment;
import io.github.protino.codewatch.ui.adapter.StatsAdapter;
import io.github.protino.codewatch.ui.widget.CustomMarkerView;
import io.github.protino.codewatch.ui.widget.PerformanceBarView;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.FileProviderUtils;
import io.github.protino.codewatch.utils.FormatUtils;
import io.github.protino.codewatch.utils.NetworkUtils;
import io.github.protino.codewatch.utils.TransformUtils;
import io.github.protino.codewatch.utils.UiUtils;
import timber.log.Timber;

import static io.github.protino.codewatch.utils.Constants.INTERNET_OFF;
import static io.github.protino.codewatch.utils.Constants.NONE;
import static io.github.protino.codewatch.utils.Constants.STATS_UPDATING;
import static io.github.protino.codewatch.utils.Constants.UNKNOWN_ERROR;

/**
 * @author Gurupad Mamadapur
 */

public class DashboardFragment extends ChartFragment implements SwipeRefreshLayout.OnRefreshListener {

    //@formatter:off
    //activity
    @BindView(R.id.linechart_activity)
    public LineChart lineChart;
    @BindView(R.id.activity_total)
    public TextView todaysLogTimeText;

    //performance
    @BindView(R.id.performance_bar)
    public PerformanceBarView performanceBarView;
    @BindView(R.id.daily_average_text)
    public TextView dailyAverageText;
    @BindView(R.id.today_log_percent_text)
    public TextView todaysLogPercentText;

    //languages
    @BindView(R.id.piechart_languages)
    public PieChart pieChartLanguages;
    @BindView(R.id.expand_piechart_language)
    public ImageView expandLanguages;
    @BindView(R.id.list_languages)
    public RecyclerView languagesListview;
    @State
    public Float languageHighlightedEntryX = null;

    //editors
    @BindView(R.id.piechart_editors)
    public PieChart pieChartEditors;
    @BindView(R.id.expand_piechart_editors)
    public ImageView expandEditors;
    @BindView(R.id.list_editors)
    public RecyclerView editorsListView;
    @State
    public Float editorHighlightedEntryX = null;

    //os
    @BindView(R.id.piechart_os)
    public PieChart pieChartOs;
    @BindView(R.id.expand_piechart_os)
    public ImageView expandOs;
    @BindView(R.id.list_os)
    public RecyclerView osListview;
    @State
    public Float osHighlightedEntryX = null;

    @BindView(R.id.adView)
    public AdView adView;
    @BindView(R.id.scroll_view)
    public ScrollView scrollView;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindArray(R.array.chart_colors)
    public int[] chartColors;

    @State
    public String firebaseUid;
    @State
    public Integer scrollPosition;
    //@formatter:on

    //firebase data
    private Stats stats;


    private SparseBooleanArray isExpandedMap = new SparseBooleanArray();
    private Context context;
    private DatabaseReference statsDatabaseRef;
    private DatabaseReference projectsDatabaseRef;
    private ValueEventListener statsValueEventListener;
    private View rootView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (scrollView != null) {
            scrollPosition = scrollView.getScrollY();
        }

        Icepick.saveInstanceState(this, outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, rootView);
        Icepick.restoreInstanceState(this, savedInstanceState);

        context = getActivity();
        setContext(context);
        setChartColors(chartColors);

        if (savedInstanceState == null) {
            firebaseUid = CacheUtils.getFirebaseUserId(context);
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        statsDatabaseRef = firebaseDatabase.getReference().child("users").child(firebaseUid).child("stats");
        projectsDatabaseRef = firebaseDatabase.getReference().child("users").child(firebaseUid).child("timeSpentOnProjects");

        setUpActivityChart();
        setListeners();
        swipeRefreshLayout.setRefreshing(true);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (scrollPosition != null && scrollView != null) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, scrollPosition);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        attachValueEventListener();
    }

    @Override
    public void onPause() {
        detachValueEventListener();
        super.onPause();
    }

    @OnClick({R.id.share_activity_chart, R.id.share_editors_chart, R.id.share_language_chart, R.id.share_os_chart})
    public void onShareClick(View view) {
        Bitmap bitmap = null;
        switch (view.getId()) {
            case R.id.share_activity_chart:
                bitmap = lineChart.getChartBitmap();
                break;
            case R.id.share_editors_chart:
                bitmap = pieChartEditors.getChartBitmap();
                break;
            case R.id.share_language_chart:
                bitmap = pieChartLanguages.getChartBitmap();
                break;
            case R.id.share_os_chart:
                bitmap = pieChartOs.getChartBitmap();
                break;
            default:
                break;
        }
        if (bitmap != null) {
            //add watermark
            bitmap = UiUtils.addWaterMark(bitmap, context);

            try {
                FileProviderUtils.shareBitmap(context, bitmap);
            } catch (IOException e) {
                Timber.d(e, "IO Error while saving image");
                Toast.makeText(context, R.string.share_error, Toast.LENGTH_SHORT).show();
            }
            bitmap.recycle();
        }
    }

    private void attachValueEventListener() {
        if (statsValueEventListener == null) {
            statsValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    stats = dataSnapshot.getValue(Stats.class);
                    bindViews();
                    updateWidgets();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        statsDatabaseRef.addValueEventListener(statsValueEventListener);
    }

    private void detachValueEventListener() {
        if (statsValueEventListener != null) {
            statsDatabaseRef.removeEventListener(statsValueEventListener);
            statsValueEventListener = null;
        }
    }

    private void setUpPerformanceBar() {
        final int todaysTotalSeconds = stats.getTodaysTotalSeconds();
        final float dailyAverage = stats.getDailyAverageSeconds();

        final float todayLogPercent = (todaysTotalSeconds / dailyAverage) * 100f;

        performanceBarView.setProgress(todaysTotalSeconds);
        performanceBarView.setGoal(dailyAverage);

        dailyAverageText.setText(context.getString(R.string.daily_average_format,
                FormatUtils.getFormattedTime(context, (int) dailyAverage)));
        todaysLogPercentText.setText(context.getString(R.string.todays_log_percent_text, todayLogPercent));
        todaysLogTimeText.setText(context.getString(R.string.todays_total_log_time,
                FormatUtils.getFormattedTime(context, todaysTotalSeconds)));
    }

    private void setUpActivityChart() {

        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextColor(Color.WHITE);
        l.setTextSize(12);
        l.setWordWrapEnabled(true);
        l.setXEntrySpace(UiUtils.dpToPx(4));
        l.setYEntrySpace(UiUtils.dpToPx(4));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value == 0 ? "" : String.valueOf(FormatUtils.getFormattedTime(context, (int) value));
            }
        });
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(3600f); // FIXME: 12-04-2017 granularity not respected
        leftAxis.setAxisLineWidth(2f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);
        xAxis.setYOffset(UiUtils.dpToPx(4));
        xAxis.setAxisLineWidth(2f);
        xAxis.setAxisLineColor(Color.WHITE);

        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackground(context.getResources().getDrawable(R.color.colorPrimaryDark));
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDragDecelerationEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setExtraOffsets(16, 0, 16, 0);
    }

    private void bindViews() {
        bindActivityChart();
        setUpPerformanceBar();
        bindPieCharts();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void bindPieCharts() {

        setUpPieChart(pieChartLanguages, stats.getLanguagesMap(), LANGUAGE_CHART_ID);
        setUpPieChart(pieChartEditors, stats.getEditorsMap(), EDITORS_CHART_ID);
        setUpPieChart(pieChartOs, stats.getOsMap(), OS_CHART_ID);

        setUpOnExpandRecyclerView(languagesListview, LANGUAGE_CHART_ID);
        setUpOnExpandRecyclerView(editorsListView, EDITORS_CHART_ID);
        setUpOnExpandRecyclerView(osListview, OS_CHART_ID);

        pieChartLanguages.animateXY(1500, 1500);
        pieChartEditors.animateXY(1500, 1500);
        pieChartOs.animateXY(1500, 1500);

        setListeners();

        isExpandedMap.clear();
        if (languageHighlightedEntryX != null) {
            pieChartLanguages.highlightValue(new Highlight(languageHighlightedEntryX, Float.NaN, 0), true);
        }

        if (editorHighlightedEntryX != null) {
            pieChartEditors.highlightValue(new Highlight(editorHighlightedEntryX, Float.NaN, 0), true);
        }

        if (osHighlightedEntryX != null) {
            pieChartOs.highlightValue(new Highlight(osHighlightedEntryX, Float.NaN, 0), true);
        }
    }

    private void bindActivityChart() {
        LineData lineData = generateActivityLineData();
        lineChart.setData(lineData);
        int maxYData = (int) (Math.ceil(lineData.getYMax()) + 7200);
        lineChart.getAxisLeft().setAxisMaximum(maxYData);
        long referenceTime = new DateTime(stats.endDate).minusDays(6).getMillis();

        lineChart.getXAxis()
                .setValueFormatter(new FormatUtils().getBarXAxisValueFormatterInstance(referenceTime));

        CustomMarkerView customMarkerView = new CustomMarkerView(context, R.layout.marker_view, referenceTime);
        lineChart.setMarker(customMarkerView);
        customMarkerView.setChartView(lineChart);

        lineChart.animateX(1500, Easing.EasingOption.Linear);
    }

    private void setListeners() {
        pieChartLanguages.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(LANGUAGE_CHART_ID));
        pieChartEditors.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(EDITORS_CHART_ID));
        pieChartOs.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(OS_CHART_ID));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private LineData generateActivityLineData() {
        LineData lineData = new LineData();
        List<Map<String, Integer>> pairList = stats.getProjectPairList();
        Map<String, List<Entry>> projectsData = transformProjectData(pairList);

        int index = 0;
        for (Map.Entry<String, List<Entry>> entries : projectsData.entrySet()) {
            LineDataSet set = new LineDataSet(entries.getValue(), entries.getKey());
            formatLineDataSet(set, chartColors[index++], 3);
            lineData.addDataSet(set);
        }
        return lineData;
    }

    private Map<String, List<Entry>> transformProjectData(List<Map<String, Integer>> pairList) {
        final Map<String, List<Entry>> listMap = new HashMap<>();
        final Map<String, Integer> timeSpentMap = new HashMap<>();

        /*
         When on a holiday or zero seconds have been logged
         pairList does not contain sequential indices */

        for (int i = 0; i < pairList.size(); i++) {
            Map<String, Integer> map = pairList.get(i);
            if (map == null) {
                continue;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                List<Entry> totalSeconds = listMap.get(entry.getKey());
                if (totalSeconds != null) {
                    totalSeconds.add(new Entry(i, entry.getValue()));
                } else {
                    totalSeconds = new ArrayList<>();
                }
                listMap.put(entry.getKey(), totalSeconds);
            }
        }

        for (Map.Entry<String, List<Entry>> entry : listMap.entrySet()) {
            int total = 0;
            for (Entry subEntry : entry.getValue()) {
                total += subEntry.getY();
            }
            timeSpentMap.put(entry.getKey(), total);
        }
        projectsDatabaseRef.setValue(timeSpentMap);
        return listMap;
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

    @OnClick({R.id.expand_piechart_editors, R.id.expand_piechart_language, R.id.expand_piechart_os})
    public void onExpand(View view) {
        boolean isExpanded = isExpandedMap.get(view.getId());
        Drawable drawable = ContextCompat.getDrawable(getActivity(),
                isExpanded ? R.drawable.ic_expand_more_white_24dp : R.drawable.ic_expand_less_white_24dp);
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

    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkUp(context)) {
            new StatsFetchAsyncTask().execute();
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            showSnackBar(R.string.internet_error_message);
        }
    }

    private void showSnackBar(@StringRes final int resId) {
        Snackbar.make(rootView, context.getString(resId), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //This check is needed when user navigates to other fragments and this fragment is destroyed
                        if (!isVisible()) {
                            return;
                        }

                        onRefresh();
                        if (!NetworkUtils.isNetworkUp(context)) {
                            showSnackBar(resId);
                        }
                    }
                }).show();
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
            switch (CHART_ID) {
                case LANGUAGE_CHART_ID:
                    if (!isExpandedMap.get(expandLanguages.getId())) {
                        onExpand(expandLanguages);
                    }
                    languageHighlightedEntryX = h.getX();
                    resetAdapter(languagesListview, languageListAdapter, ((PieEntry) e).getLabel());
                    break;
                case OS_CHART_ID:
                    if (!isExpandedMap.get(expandOs.getId())) {
                        onExpand(expandOs);
                    }
                    osHighlightedEntryX = h.getX();
                    resetAdapter(osListview, osListAdapter, ((PieEntry) e).getLabel());
                    break;
                case EDITORS_CHART_ID:
                    if (!isExpandedMap.get(expandEditors.getId())) {
                        onExpand(expandEditors);
                    }
                    editorHighlightedEntryX = h.getX();
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
                    languageHighlightedEntryX = null;
                    onExpand(expandLanguages);
                    break;
                case OS_CHART_ID:
                    osHighlightedEntryX = null;
                    onExpand(expandOs);
                    break;
                case EDITORS_CHART_ID:
                    editorHighlightedEntryX = null;
                    onExpand(expandEditors);
                    break;
                default:
                    break;
            }
        }
    }

    private class StatsFetchAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected
        @Constants.ErrorCodes
        Integer doInBackground(Void... params) {
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(context);
            try {
                WakatimeDataWrapper wakatimeDataWrapper = new WakatimeDataWrapper();
                fetchWakatimeData.fetchStatsOnly(wakatimeDataWrapper);
                TransformUtils transformUtils = new TransformUtils(wakatimeDataWrapper, new User());
                stats = transformUtils.transformStats().getStats();
                statsDatabaseRef.setValue(stats);
                return NONE;
            } catch (IOException e) {
                Timber.e(e);
                return INTERNET_OFF;
            } catch (NullPointerException e) {
                return STATS_UPDATING;
            } catch (Exception e) {
                return UNKNOWN_ERROR;
            }
        }

        @Override
        protected void onPostExecute(@Constants.ErrorCodes Integer result) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            switch (result) {
                case INTERNET_OFF:
                    showSnackBar(R.string.internet_error_message);
                    break;
                case STATS_UPDATING:
                    showSnackBar(R.string.stats_updating_error_message);
                    break;
                case UNKNOWN_ERROR:
                    FirebaseCrash.log("Unknown error occurred while fetching stats - error code" + UNKNOWN_ERROR);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateWidgets() {
        Intent intent = new Intent(Constants.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
