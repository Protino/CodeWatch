package io.github.protino.codewatch.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.firebase.LanguageGoal;
import io.github.protino.codewatch.model.firebase.ProjectGoal;
import io.github.protino.codewatch.ui.widget.BarChartMarkerView;
import io.github.protino.codewatch.ui.widget.PerformanceBarView;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.FormatUtils;
import io.github.protino.codewatch.utils.UiUtils;

import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class GoalsDetailFragment extends DialogFragment {

    //@formatter:off
    @BindView(R.id.goal_text) public TextView goalText;
    @BindView(R.id.progressContent) public LinearLayout progressContent;
    @BindView(R.id.remainingDays) public TextView remainingDays;
    @BindView(R.id.progressBar) public PerformanceBarView progressBarView;
    @BindView(R.id.goal_chart) public BarChart goalBarChart;
    @BindColor(R.color.grey_800) public int darkGrey;
    @BindColor(R.color.green_400) public int green400;
    @BindColor(R.color.red_400) public int red400;
    @BindColor(R.color.colorAccent) public int accentColor;
    @BindColor(R.color.blue_400) public int blue400;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.status_bar) View statusBar;
    //@formatter:on
    private int goalType;
    private String goalData;
    private LanguageGoal languageGoal;
    private ProjectGoal projectGoal;
    private Context context;
    private long referenceTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goalData = getArguments().getString(Constants.GOAL_DATA_KEY, null);
        goalType = getArguments().getInt(Constants.GOAL_TYPE_KEY, -1);

        if (goalData == null || goalType == -1) {
            throw new IllegalArgumentException("Incorrect data passed");
        }

        //fetch data
        switch (goalType) {
            case LANGUAGE_GOAL:
                //fetch data
                languageGoal = new Gson().fromJson(goalData, LanguageGoal.class);
                break;
            case PROJECT_DAILY_GOAL:
            case PROJECT_DEADLINE_GOAL:
                projectGoal = new Gson().fromJson(goalData, ProjectGoal.class);
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

        ButterKnife.bind(this, rootView);
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
                        context, projectGoal.getProjectName(), projectGoal.getDeadline() * 1000L));
                setUpProgressBar();
                break;
            default:
                throw new IllegalArgumentException("Incorrect goal type");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(5); //todo check
            handleStatusBar();
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

        return rootView;
    }

    private void handleStatusBar() {
        statusBar.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.getStatusBarHeight(context)));
    }

    private void setUpBarChart(int dailyGoal, int[] progressSoFar) {

        BarData barData = generateBarData(dailyGoal, progressSoFar);

        LimitLine limitLine = new LimitLine(dailyGoal * 60 * 60, context.getString(R.string.goal));
        limitLine.setLineWidth(4f);
        limitLine.setLineColor(blue400);
        limitLine.setTextSize(12f);
        limitLine.setTextColor(Color.BLACK);

        XAxis xAxis = goalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new FormatUtils().getBarXAxisValueFormatterInstance(referenceTime));
        //xAxis.setSpaceMin(36288f);
        //xAxis.setSpaceMax(36288f);
        xAxis.setAxisLineWidth(2f);

        YAxis leftAxis = goalBarChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limitLine);
        leftAxis.setDrawGridLines(false);
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

        int maximum;
        int maxYData = (int) TimeUnit.SECONDS.toHours((long) Math.ceil(barData.getYMax()));

        maximum = (maxYData > dailyGoal) ? maxYData : dailyGoal;

        maximum += 2;

        leftAxis.setAxisMaximum(maximum * 60 * 60);
        leftAxis.setAxisMinimum(0f);

        BarChartMarkerView barChartMarkerView = new BarChartMarkerView(context, R.layout.marker_view, referenceTime);
        goalBarChart.setMarker(barChartMarkerView);

        goalBarChart.getAxisRight().setEnabled(false);
        goalBarChart.getLegend().setEnabled(false);
        goalBarChart.getDescription().setEnabled(false);
        goalBarChart.setBackgroundColor(Color.WHITE);
        goalBarChart.setDrawGridBackground(false);
        goalBarChart.setDragEnabled(false);
        goalBarChart.setScaleEnabled(false);
        goalBarChart.setDragDecelerationEnabled(false);
        goalBarChart.setPinchZoom(false);
        goalBarChart.setDoubleTapToZoomEnabled(false);
        goalBarChart.setDrawBorders(false);
        goalBarChart.setData(barData);

        barChartMarkerView.setChartView(goalBarChart);


        goalBarChart.setVisibility(View.VISIBLE);
        goalBarChart.animateY(1500, Easing.EasingOption.Linear);
    }

    private BarData generateBarData(int dailyGoal, int[] progressSoFar) {


        ArrayList<BarEntry> barEntries = new ArrayList<>(progressSoFar.length);

        DateTime dateTime = new DateTime();
        referenceTime = dateTime.plusDays(-6).getMillis();
        for (int i = 0; i < progressSoFar.length; i++) {
            barEntries.add(new BarEntry(i, progressSoFar[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        int colors[] = new int[progressSoFar.length];
        for (int i = 0; i < progressSoFar.length; i++) {
            colors[i] = (progressSoFar[i] >= dailyGoal * 60 * 60) ? green400 : red400;
        }
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.85f);
        barData.setDrawValues(false);
        return barData;
    }

    private void setUpProgressBar() {
        progressBarView.setProgressAsDate(true);
        progressBarView.setDeadlineDate(projectGoal.getDeadline() * 1000L);
        progressBarView.setStartDate(projectGoal.getStartDate() * 1000L);
        progressBarView.setMarkerLineColor(darkGrey);
        remainingDays.setText(FormatUtils.getRemainingDaysText(context, progressBarView.getRemainingDays()));
        progressContent.setVisibility(View.VISIBLE);
    }

    //    @OnClick(R.id.edit)
//    public void onEditClick() {
//        // open edit dialog
//    }
//
    public void onDelete() {
        //delete the item
        //notify adapter to reload
        //close the dialog
        dismiss();

    }
}
