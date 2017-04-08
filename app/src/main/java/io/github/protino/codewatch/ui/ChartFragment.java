package io.github.protino.codewatch.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.github.protino.codewatch.model.PieChartItem;
import io.github.protino.codewatch.ui.adapter.SimpleAdapter;

/**
 * @author Gurupad Mamadapur
 */

public abstract class ChartFragment extends Fragment {


    public static final int LANGUAGE_CHART_ID = 1;
    public static final int ACTIVITY_CHART_ID = 2;
    public static final int OS_CHART_ID = 3;
    public static final int EDITORS_CHART_ID = 4;
    protected SimpleAdapter editorsListAdapter;
    protected SimpleAdapter languageListAdapter;
    protected SimpleAdapter osListAdapter;
    private int[] chartColors;
    private Context context;
    private List<PieChartItem> languageDataItems = new ArrayList<>();
    private List<PieChartItem> editorDataItems = new ArrayList<>();
    private List<PieChartItem> osDataItems = new ArrayList<>();

    public void setUpPieChart(PieChart pieChart, Map<String, Integer> chartData, int chartType) {
        formatPieChart(pieChart);
        formatPieChartLegend(pieChart.getLegend());
        List<PieChartItem> pieChartItems = setUpData(chartData);
        pieChart.setData(generatePieData(pieChartItems));
        pieChart.highlightValues(null);

        switch (chartType) {
            case LANGUAGE_CHART_ID:
                languageDataItems = pieChartItems;
                break;
            case EDITORS_CHART_ID:
                editorDataItems = pieChartItems;
                break;
            case OS_CHART_ID:
                osDataItems = pieChartItems;
                break;
            default:
                throw new UnsupportedOperationException("Invalid chart type");
        }
    }

    public void setUpOnExpandRecyclerView(RecyclerView recyclerView, int chartType) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        switch (chartType) {
            case LANGUAGE_CHART_ID:
                languageListAdapter = new SimpleAdapter(context, languageDataItems, "");
                recyclerView.setAdapter(languageListAdapter);
                break;
            case EDITORS_CHART_ID:
                editorsListAdapter = new SimpleAdapter(context, editorDataItems, "");
                recyclerView.setAdapter(editorsListAdapter);
                break;
            case OS_CHART_ID:
                osListAdapter = new SimpleAdapter(context, osDataItems, "");
                recyclerView.setAdapter(osListAdapter);
                break;
            default:
                throw new UnsupportedOperationException("Invalid chart type");
        }
    }

    private List<PieChartItem> setUpData(Map<String, Integer> dataMap) {

        List<String> labelList = new ArrayList<>();
        List<Integer> timeList = new ArrayList<>();
        List<PieChartItem> pieChartItems = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            labelList.add(entry.getKey());
            timeList.add(entry.getValue());
        }

        float totalSum = 0;
        for (Integer integer : timeList) {
            totalSum += integer;
        }

        PieChartItem item;
        for (int i = 0; i < labelList.size(); i++) {
            item = new PieChartItem();
            item.setName(labelList.get(i));
            item.setPercent((timeList.get(i) / totalSum) * 100);
            item.setTime(timeList.get(i));
            pieChartItems.add(i, item);
        }
        Collections.sort(pieChartItems, new Comparator<PieChartItem>() {
            @Override
            public int compare(PieChartItem o1, PieChartItem o2) {
                return o2.getTime() - o1.getTime();
            }
        });
        return pieChartItems;
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

    protected void toggleListViewVisibility(boolean expand, RecyclerView view, int chartType) {
        SimpleAdapter adapter = new SimpleAdapter(context, new ArrayList<PieChartItem>(), "");

        switch (chartType) {
            case LANGUAGE_CHART_ID:
                adapter = languageListAdapter;
                break;
            case EDITORS_CHART_ID:
                adapter = editorsListAdapter;
                break;
            case OS_CHART_ID:
                adapter = osListAdapter;
                break;
            default:
                break;
        }

        view.setVisibility(expand ? View.VISIBLE : View.GONE);
        if (!expand) {
            view.setAdapter(new SimpleAdapter(context, adapter.getItemList(), ""));
        }
    }

    protected void handleHighlights(PieChart pieChart, boolean isExpanded) {
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
    protected void resetAdapter(RecyclerView view, SimpleAdapter adapter, String label) {
        adapter = new SimpleAdapter(context, adapter.getItemList(), label);
        view.setAdapter(adapter);
    }

    public void setChartColors(int[] chartColors) {
        this.chartColors = chartColors;
    }

    public void setContext(Context context) {
        this.context = context;
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
}
