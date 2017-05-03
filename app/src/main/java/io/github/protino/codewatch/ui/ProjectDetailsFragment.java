package io.github.protino.codewatch.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.crash.FirebaseCrash;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.bundler.ProjectBundler;
import io.github.protino.codewatch.model.firebase.Project;
import io.github.protino.codewatch.model.project.summary.Editor;
import io.github.protino.codewatch.model.project.summary.Language;
import io.github.protino.codewatch.model.project.summary.OperatingSystem;
import io.github.protino.codewatch.model.project.summary.ProjectSummaryData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.ui.widget.CustomMarkerView;
import io.github.protino.codewatch.utils.FileProviderUtils;
import io.github.protino.codewatch.utils.FormatUtils;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectDetailsFragment extends ChartFragment {

    private static final String SAMPLE_PROJECT_ID = "0f626e4d-8f24-4320-a355-0614d6b3aab2";
    //@formatter:off
    //languages
    @BindView(R.id.piechart_languages) public PieChart pieChartLanguages;
    @BindView(R.id.expand_piechart_language) public ImageView expandLanguages;
    @BindView(R.id.list_languages) public RecyclerView languagesListview;
    @State public Float languageHighlightedEntryX = null;

    //editors
    @BindView(R.id.piechart_editors) public PieChart pieChartEditors;
    @BindView(R.id.expand_piechart_editors) public ImageView expandEditors;
    @BindView(R.id.list_editors) public RecyclerView editorsListView;
    @State public Float editorHighlightedEntryX = null;

    //os
    @BindView(R.id.piechart_os) public PieChart pieChartOs;
    @BindView(R.id.expand_piechart_os) public ImageView expandOs;
    @BindView(R.id.list_os) public RecyclerView osListview;
    @BindArray(R.array.chart_colors) public int[] chartColors;
    @BindColor(R.color.blue_400) public int blue400;
    @State public Float osHighlightedEntryX = null;

    @State(ProjectBundler.class) public Project project;
    @State public String projectName;
    @State public Integer scrollPosition;


    @BindView(R.id.content) View content;
    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.progressBarLayout) View progressBarLayout;
    @BindView(R.id.barchart) BarChart barChart;
    @BindView(R.id.activity_total) TextView activityTotal;
    //@formatter:on
    private SparseBooleanArray isExpandedMap = new SparseBooleanArray();

    private Context context;

    private long referenceTime;
    private Unbinder unbinder;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        projectName = getArguments().getString(Intent.EXTRA_TEXT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_project_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        context = getActivity();
        hideProgressBar(false);

        setChartColors(chartColors);
        setContext(context);

        if (project == null) {
            new FetchProjectDetails().execute(projectName);
        } else {
            hideProgressBar(true);
            displayData();
        }
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
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }


    private void hideProgressBar(boolean hide) {
        progressBarLayout.setVisibility(hide ? View.GONE : View.VISIBLE);
        content.setVisibility(hide ? View.VISIBLE : View.GONE);
    }

    private void displayData() {
        setUpBarChart();

        setUpPieChart(pieChartLanguages, project.getLanguageList(), LANGUAGE_CHART_ID);
        setUpPieChart(pieChartEditors, project.getEditorPaiList(), EDITORS_CHART_ID);
        setUpPieChart(pieChartOs, project.getOsPairList(), OS_CHART_ID);

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

    private void setUpBarChart() {
        BarData barData = generateBarData(project.getTimeSpent());

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new FormatUtils().getBarXAxisValueFormatterInstance(referenceTime));
        xAxis.setAxisLineWidth(2f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGranularity(60 * 60);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                int hours = (int) TimeUnit.SECONDS.toHours((long) Math.ceil(value));

                return (hours == 0)
                        ? "" : context.getString(R.string.hours, hours);
            }
        });
        leftAxis.setAxisLineWidth(2f);

        int maxYData = (int) (Math.ceil(barData.getYMax()) + 3600);

        leftAxis.setAxisMaximum(maxYData);
        leftAxis.setAxisMinimum(0f);

        CustomMarkerView customMarkerView = new CustomMarkerView(context, R.layout.marker_view, referenceTime);
        barChart.setMarker(customMarkerView);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setBackground(context.getResources().getDrawable(R.color.colorPrimaryDark));
        barChart.setDrawGridBackground(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDragDecelerationEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setData(barData);

        customMarkerView.setChartView(barChart);

        barChart.setVisibility(View.VISIBLE);
        barChart.animateY(1500, Easing.EasingOption.Linear);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (scrollView != null) {
            scrollPosition = scrollView.getScrollY();
        }
        Icepick.saveInstanceState(this, outState);
    }


    private BarData generateBarData(List<Integer> progressSoFar) {

        ArrayList<BarEntry> barEntries = new ArrayList<>(progressSoFar.size());

        DateTime dateTime = new DateTime();
        referenceTime = dateTime.minusDays(7).getMillis();
        int totalSeconds = 0;
        for (int i = 0; i < progressSoFar.size(); i++) {
            int progress = progressSoFar.get(i);
            barEntries.add(new BarEntry(i, progress));
            totalSeconds += progress;
        }

        activityTotal.setText(context.getString(R.string.total_project_time,
                FormatUtils.getFormattedTime(context, totalSeconds)));

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        barDataSet.setColors(blue400);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.85f);
        barData.setDrawValues(false);
        return barData;
    }

    private void setListeners() {
        pieChartLanguages.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(LANGUAGE_CHART_ID));
        pieChartEditors.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(EDITORS_CHART_ID));
        pieChartOs.setOnChartValueSelectedListener(new CustomOnValueSelectedListener(OS_CHART_ID));
    }

    @OnClick({R.id.expand_piechart_editors, R.id.expand_piechart_language, R.id.expand_piechart_os})
    public void onExpand(View view) {
        boolean isExpanded = isExpandedMap.get(view.getId());
        Drawable drawable = ContextCompat.getDrawable(context,
                isExpanded ? R.drawable.ic_expand_more_white_24dp : R.drawable.ic_expand_less_white_24dp);
        isExpandedMap.put(view.getId(), !isExpanded);
        switch (view.getId()) {
            case R.id.expand_piechart_language:
                expandLanguages.setImageDrawable(drawable);
                handleHighlights(pieChartLanguages, isExpanded);
                toggleListViewVisibility(!isExpanded, languagesListview, LANGUAGE_CHART_ID);
                break;
            case R.id.expand_piechart_editors:
                expandEditors.setImageDrawable(drawable);
                handleHighlights(pieChartEditors, isExpanded);
                toggleListViewVisibility(!isExpanded, editorsListView, EDITORS_CHART_ID);
                break;
            case R.id.expand_piechart_os:
                expandOs.setImageDrawable(drawable);
                handleHighlights(pieChartEditors, isExpanded);
                toggleListViewVisibility(!isExpanded, osListview, OS_CHART_ID);
                break;
            default://ignore
                break;
        }
    }

    @OnClick({R.id.share_activity_chart, R.id.share_editors_chart, R.id.share_language_chart, R.id.share_os_chart})
    public void onShareClick(View view) {
        Bitmap bitmap = null;
        switch (view.getId()) {
            case R.id.share_activity_chart:
                bitmap = barChart.getChartBitmap();
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
            try {
                FileProviderUtils.shareBitmap(context, bitmap);
            } catch (IOException e) {
                Timber.d(e, "IO Error while saving image");
                Toast.makeText(context, R.string.share_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchProjectDetails extends AsyncTask<String, Void, Project> {

        /* IMPORTANT! For some reason the api endpoint of wakatime requires project name
           instead of project id.
         */
        private String projectName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideProgressBar(false);
        }

        @Override
        protected Project doInBackground(String... params) {
            long start = System.currentTimeMillis();
            Project project = new Project();
            projectName = params[0];

            List<Integer> timeSpent = new ArrayList<>(7);

            Map<String, Integer> languageStats = new HashMap<>();
            Map<String, Integer> editorStats = new HashMap<>();
            Map<String, Integer> osStats = new HashMap<>();


            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(context);
            try {
                List<ProjectSummaryData> dataList = fetchWakatimeData.fetchProjectSummary(projectName).getData();
                Timber.d("Download time - " + (System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
                for (ProjectSummaryData summaryData : dataList) {
                    timeSpent.add(summaryData.getGrandTotal().getTotalSeconds());

                    List<Language> languageList = summaryData.getLanguages();
                    List<Editor> editorList = summaryData.getEditors();
                    List<OperatingSystem> osList = summaryData.getOperatingSystems();

                    Map<String, Integer> map = new HashMap<>();
                    for (Language language : languageList) {
                        map.put(language.getName(), language.getTotalSeconds());
                    }
                    sumMaps(languageStats, map);

                    map.clear();
                    for (Editor editor : editorList) {
                        map.put(editor.getName(), editor.getTotalSeconds());
                    }
                    sumMaps(editorStats, map);

                    map.clear();
                    for (OperatingSystem operatingSystem : osList) {
                        map.put(operatingSystem.getName(), operatingSystem.getTotalSeconds());
                    }
                    sumMaps(osStats, map);
                }

                project.setName(projectName);
                project.setTimeSpent(timeSpent);
                project.setLanguageList(languageStats);
                project.setEditorPaiList(editorStats);
                project.setOsPairList(osStats);
                Timber.d("Parse time " + String.valueOf(System.currentTimeMillis() - start));

            } catch (IOException e) {
                FirebaseCrash.report(e);
                return null;
            } catch (Exception e) {
                FirebaseCrash.report(e);
                return null;
            }
            return project;
        }

        private void sumMaps(Map<String, Integer> original, Map<String, Integer> newMap) {

            if (newMap != null) {
                for (Map.Entry<String, Integer> entry : newMap.entrySet()) {
                    Integer previousValue = original.get(entry.getKey());
                    if (previousValue == null) {
                        previousValue = 0;
                    }
                    original.put(entry.getKey(), entry.getValue() + previousValue);
                }
            }
        }


        @Override
        protected void onPostExecute(Project result) {
            hideProgressBar(true);
            if (result != null) {
                project = result;
                displayData();
            } else {
                Snackbar.make(rootView, R.string.internet_error_message, Snackbar.LENGTH_LONG).show();
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
}
