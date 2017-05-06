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

package io.github.protino.codewatch.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindBool;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.model.firebase.LanguageGoal;
import io.github.protino.codewatch.model.firebase.ProjectGoal;
import io.github.protino.codewatch.ui.widget.CustomMarkerView;
import io.github.protino.codewatch.ui.widget.PerformanceBarView;
import io.github.protino.codewatch.utils.FormatUtils;
import io.github.protino.codewatch.utils.UiUtils;

import static io.github.protino.codewatch.ui.GoalsFragment.GOAL_DATA_KEY;
import static io.github.protino.codewatch.ui.GoalsFragment.GOAL_ITEM_KEY;
import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * Displays details of the goal depending on its type.
 *
 * @author Gurupad Mamadapur
 */

public class GoalsDetailFragment extends DialogFragment {

    //@formatter:off
    @BindView(R.id.goal_text) public TextView goalText;
    @BindView(R.id.progressContent) public LinearLayout progressContent;
    @BindView(R.id.remainingDays) public TextView remainingDays;
    @BindView(R.id.progressBar) public PerformanceBarView progressBarView;
    @BindView(R.id.goal_chart) public BarChart goalBarChart;
    @BindColor(R.color.blue_400) public int blue400;
    @BindColor(R.color.window_background) public int windowColor;
    @BindColor(R.color.green_400) public int green400;
    @BindColor(R.color.red_400) public int red400;
    @BindColor(R.color.colorAccent) public int accentColor;
    @BindBool(R.bool.isLargeDevice) public boolean isLargeDevice;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.status_bar) View statusBar;
    //@formatter:on
    private GoalItem goalItem;
    private int goalType;
    private LanguageGoal languageGoal;
    private ProjectGoal projectGoal;
    private Context context;
    private long referenceTime;

    private OnDeleteListener onDeleteListener;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String gsonData = getArguments().getString(GOAL_DATA_KEY, null);
        goalItem = new Gson().fromJson(getArguments().getString(GOAL_ITEM_KEY, null), GoalItem.class);

        if (gsonData == null || goalItem == null) {
            throw new IllegalArgumentException("Incorrect data passed");
        }

        goalType = goalItem.getType();

        //fetch data
        switch (goalType) {
            case LANGUAGE_GOAL:
                languageGoal = new Gson().fromJson(gsonData, LanguageGoal.class);
                break;
            case PROJECT_DAILY_GOAL:
            case PROJECT_DEADLINE_GOAL:
                projectGoal = new Gson().fromJson(gsonData, ProjectGoal.class);
                break;
            default:
                throw new IllegalArgumentException("Incorrect data passed");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_detail, container, false);

        context = getActivity();

        unbinder = ButterKnife.bind(this, rootView);
        switch (goalType) {
            case LANGUAGE_GOAL:
                goalText.setText(context.getString(
                        R.string.language_goal, languageGoal.getGoalId(), languageGoal.getDailyGoal()));
                setUpBarChart(languageGoal.getDailyGoal(), languageGoal.getProgressSoFar());
                break;
            case PROJECT_DAILY_GOAL:
                goalText.setText(context.getString(
                        R.string.project_daily_goal, projectGoal.getDailyGoal(), projectGoal.getProjectName()));
                setUpBarChart(projectGoal.getDailyGoal(), projectGoal.getProgressSoFar());
                break;
            case PROJECT_DEADLINE_GOAL:
                goalText.setText(FormatUtils.getDeadlineGoalText(
                        context, projectGoal.getProjectName(), projectGoal.getDeadline()));
                setUpProgressBar();
                break;
            default:
                throw new IllegalArgumentException("Incorrect goal type");
        }

        // Display fake status bar only on smaller layout because it is open as a separate fragment.
        if (!isLargeDevice) {
            handleStatusBar();
        }
        setUpToolbar();

        return rootView;
    }

    private void setUpToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(context.getResources().getDimension(R.dimen.appbar_elevation));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.inflateMenu(R.menu.goal_detail_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete_action) {
                    onDelete();
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    /**
     * Set the height of the status bar appropriately
     */
    private void handleStatusBar() {
        statusBar.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.getStatusBarHeight(context)));
    }

    private void setUpBarChart(int dailyGoal, List<Integer> progressSoFar) {

        BarData barData = generateBarData(dailyGoal, progressSoFar);

        //Set up a limit line indicating the goal
        LimitLine limitLine = new LimitLine(dailyGoal * 60 * 60, context.getString(R.string.goal));
        limitLine.setLineWidth(4f);
        limitLine.setLineColor(blue400);
        limitLine.setTextSize(12f);
        limitLine.setTextColor(Color.WHITE);

        //Format Xaxis
        XAxis xAxis = goalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new FormatUtils().getBarXAxisValueFormatterInstance(referenceTime));
        xAxis.setAxisLineWidth(2f);

        //Format Yaxis
        YAxis leftAxis = goalBarChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limitLine);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGranularity(60 * 60);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                /* Do not display zero value and format the value as time*/
                return (value == 0)
                        ? "" : FormatUtils.getFormattedTime(context, (int) value);
            }
        });
        leftAxis.setAxisLineWidth(2f);

        //Set a maximum value on purpose so that marker view is visible
        int maximum;
        int maxYData = (int) TimeUnit.SECONDS.toHours((long) Math.ceil(barData.getYMax()));
        maximum = (maxYData > dailyGoal) ? maxYData : dailyGoal;
        maximum += 2;
        leftAxis.setAxisMaximum(maximum * 60 * 60);
        leftAxis.setAxisMinimum(0f);

        // Setup a marker view to display details of the selected dataItem
        CustomMarkerView customMarkerView = new CustomMarkerView(context, R.layout.marker_view, referenceTime);
        goalBarChart.setMarker(customMarkerView);

        // Disable interactions
        goalBarChart.getAxisRight().setEnabled(false);
        goalBarChart.getLegend().setEnabled(false);
        goalBarChart.getDescription().setEnabled(false);
        goalBarChart.setBackgroundColor(windowColor);
        goalBarChart.setDrawGridBackground(false);
        goalBarChart.setDragEnabled(false);
        goalBarChart.setScaleEnabled(false);
        goalBarChart.setDragDecelerationEnabled(false);
        goalBarChart.setPinchZoom(false);
        goalBarChart.setDoubleTapToZoomEnabled(false);
        goalBarChart.setDrawBorders(false);
        goalBarChart.setData(barData);

        customMarkerView.setChartView(goalBarChart);

        goalBarChart.setVisibility(View.VISIBLE);
        goalBarChart.animateY(1500, Easing.EasingOption.Linear);
    }

    private BarData generateBarData(int dailyGoal, List<Integer> progressSoFar) {


        ArrayList<BarEntry> barEntries = new ArrayList<>(progressSoFar.size());

        DateTime dateTime = new DateTime();
        referenceTime = dateTime.minusDays(7).getMillis();
        for (int i = 0; i < progressSoFar.size(); i++) {
            barEntries.add(new BarEntry(i, progressSoFar.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        int colors[] = new int[progressSoFar.size()];
        for (int i = 0; i < progressSoFar.size(); i++) {
            colors[i] = (progressSoFar.get(i) >= dailyGoal * 60 * 60) ? green400 : red400;
        }
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.85f);
        barData.setDrawValues(false);
        return barData;
    }

    private void setUpProgressBar() {
        progressBarView.setProgressAsDate(true);
        progressBarView.setDeadlineDate(projectGoal.getDeadline());
        progressBarView.setStartDate(projectGoal.getStartDate());
        progressBarView.setMarkerLineColor(Color.WHITE);
        remainingDays.setText(FormatUtils.getRemainingDaysText(context, progressBarView.getRemainingDays()));
        progressContent.setVisibility(View.VISIBLE);
    }

    public void onDelete() {
        if (onDeleteListener != null) {
            onDeleteListener.onDeleteSelected(goalItem);
        }
        dismiss();
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDeleteSelected(GoalItem goalItem);
    }
}
