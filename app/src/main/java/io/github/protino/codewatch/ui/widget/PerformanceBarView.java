package io.github.protino.codewatch.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import io.github.protino.codewatch.R;

/**
 * Used to draw a static progress bar which also
 * responds to touch events and has custom content descriptions.
 *
 * @author Gurupad Mamadapur
 */

public class PerformanceBarView extends View {

    private static final int DEFAULT_BAR_NEGATIVE_COLOR = Color.parseColor("#EF5350"); //RED_400
    private static final int DEFAULT_BAR_POSITIVE_COLOR = Color.parseColor("#4CAF50"); //GREEN_500
    private static final float DEFAULT_BAR_HEIGHT = 46f;
    private static final float DEFAULT_BAR_WIDTH = -1f; //-1 indicates to match_parent
    private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#424242"); //GREY_700
    private static final float DEFAULT_CORNER_RADIUS = 0f;
    private static final float DEFAULT_BORDER_THICKNESS = 1f;
    private static final boolean DEFAULT_SHADOW_ENABLED = false;
    private static final float DEFAULT_GOAL = 0;
    private static final float DEFAULT_PROGRESS = 0;
    private static String DEFAULT_CONTENT_DESCRIPTION = null;
    private int barPositiveColor;
    private int barNegativeColor;
    private int barBorderColor;
    private float barBorderThickness;
    private float barHeight;
    private float barWidth;
    private float cornerRadius;
    private boolean shadowEnabled;
    private float goal;
    private float progress;
    private float change;

    private Paint progressBarPaint;
    private Paint barBorderPaint;
    private Paint whitePaint;

    //dimensions
    private float progressBarTop;
    private float progressBarRight;
    private float progressBarLeft;
    private float progressBarBottom;

    public PerformanceBarView(Context context) {
        super(context);
    }

    public PerformanceBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setupPaint();
    }

    public PerformanceBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(attrs);
        setupPaint();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public PerformanceBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupAttributes(attrs);
        setupPaint();
    }

    private void setupAttributes(AttributeSet attrs) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PerformanceBarView, 0, 0);
        //Now extract custom attributes into member variables
        try {
            barBorderColor = array.getColor(
                    R.styleable.PerformanceBarView_barBorderColor, DEFAULT_BORDER_COLOR);
            barPositiveColor = array.getColor(
                    R.styleable.PerformanceBarView_barPositiveColor, DEFAULT_BAR_POSITIVE_COLOR);
            barNegativeColor = array.getColor(
                    R.styleable.PerformanceBarView_barNegativeColor, DEFAULT_BAR_NEGATIVE_COLOR);

            barBorderThickness = array.getDimension(
                    R.styleable.PerformanceBarView_barBorderThickness, DEFAULT_BORDER_THICKNESS);
            barHeight = array.getDimension(
                    R.styleable.PerformanceBarView_barHeight, DEFAULT_BAR_HEIGHT);
            barWidth = array.getDimension(
                    R.styleable.PerformanceBarView_barWidth, DEFAULT_BAR_WIDTH);
            cornerRadius = array.getDimension(
                    R.styleable.PerformanceBarView_cornerRadius, DEFAULT_CORNER_RADIUS);

            shadowEnabled = array.getBoolean(
                    R.styleable.PerformanceBarView_shadowEnabled, DEFAULT_SHADOW_ENABLED);

            goal = array.getFloat(R.styleable.PerformanceBarView_goal, DEFAULT_GOAL);
            progress = array.getFloat(R.styleable.PerformanceBarView_progress, DEFAULT_PROGRESS);
            calculateChange();

        } finally {
            array.recycle();
        }
    }


    private void setupPaint() {
        progressBarPaint = new Paint();
        progressBarPaint.setColor((change < 100) ? barNegativeColor : barPositiveColor);

        barBorderPaint = new Paint();
        barBorderPaint.setColor(barBorderColor);
        barBorderPaint.setStrokeWidth(barBorderThickness);
        if (shadowEnabled) {
            setLayerType(LAYER_TYPE_SOFTWARE, barBorderPaint);
            barBorderPaint.setShadowLayer(6, 2, 2, Color.parseColor("#424242"));
        }

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int minWidth = (int) (getPaddingLeft() + getPaddingRight() + barWidth + 2 * barBorderThickness);
        int resolvedWidth = resolveSizeAndState(minWidth, widthMeasureSpec, 0);

        int minHeight = (int) (barHeight + 2 * barBorderThickness + getPaddingBottom() + getPaddingTop());

        int resolvedHeight = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw bar with border first
        canvas.drawRect(
                getPaddingLeft(), getPaddingTop(),
                canvas.getWidth() - getPaddingRight(), canvas.getHeight() - getPaddingBottom(), barBorderPaint);

        //draw progress bar
        canvas.drawRect(progressBarLeft, progressBarTop, progressBarRight, progressBarBottom, progressBarPaint);

        //draw another white bar to show progress left
        canvas.drawRect(progressBarRight, progressBarTop, canvas.getWidth() - barBorderThickness, progressBarBottom, whitePaint);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        progressBarTop = barBorderThickness + getPaddingTop();
        progressBarBottom = h - barBorderThickness - getPaddingBottom();
        progressBarLeft = barBorderThickness + getPaddingLeft();
        progressBarRight = w * (change / 100) - barBorderThickness - getPaddingRight();
    }

    /**
     * @return color of the bar when performance is positive
     */
    public int getBarPositiveColor() {
        return barPositiveColor;
    }

    /**
     * @param barPositiveColor color of the bar when performance is positive
     */
    public void setBarPositiveColor(int barPositiveColor) {
        this.barPositiveColor = barPositiveColor;
        invalidateAndRequest();

    }

    /**
     * @return color of the bar when performance is positive
     */
    public int getBarNegativeColor() {
        return barNegativeColor;
    }

    /**
     * @param barNegativeColor color of the bar when performance is negative
     */
    public void setBarNegativeColor(int barNegativeColor) {
        this.barNegativeColor = barNegativeColor;
        invalidateAndRequest();

    }

    /**
     * @return border color
     */
    public int getBarBorderColor() {
        return barBorderColor;
    }

    /**
     * @param barBorderColor color of the border. Also make sure you've changed the
     *                       border width for the effect to take place.
     */
    public void setBarBorderColor(int barBorderColor) {
        this.barBorderColor = barBorderColor;
        invalidateAndRequest();

    }

    /**
     * @return height of the bar in dp
     */
    public float getBarHeight() {
        return barHeight;
    }

    /**
     * @param barHeight height of the bar in dp
     */
    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
        invalidateAndRequest();

    }

    /**
     * @return width of the bar in dp
     */
    public float getBarWidth() {
        return barWidth;
    }

    /**
     * @param barWidth width of the bar in dp
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
        invalidateAndRequest();

    }

    public boolean isShadowEnabled() {
        return shadowEnabled;
    }

    /**
     * If set to true a small shadow is displayed below the bar.
     *
     * @param shadowEnabled true to enable shadow
     */
    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
        invalidateAndRequest();

    }


    /**
     * @return radius in dp of the corners
     */
    public float getCornerRadius() {
        return cornerRadius;
    }

    /**
     * @param cornerRadius radius in dp of the corners
     */
    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidateAndRequest();
    }

    /**
     * @return thickness of the bar border
     */
    public float getBarBorderThickness() {
        return barBorderThickness;
    }

    public void setBarBorderThickness(int barBorderThickness) {
        this.barBorderThickness = barBorderThickness;
        invalidateAndRequest();
    }

    /**
     * @return current goal
     */
    public float getGoal() {
        return goal;
    }

    /**
     * @param goal value representing the goal that is to be reached for 100% performance
     */
    public void setGoal(float goal) {
        this.goal = goal;
        calculateChange();
        invalidateAndRequest();
    }

    public float getProgress() {
        return progress;
    }

    /**
     * @param progress value representing the current amount of work done
     */
    public void setProgress(float progress) {
        this.progress = progress;
        calculateChange();
        invalidateAndRequest();
    }

    public float getChange() {
        return change;
    }

    private void calculateChange() {
        if (progress < goal) {
            change = (progress / goal) * 100f;
        } else {
            change = 100f;
        }
        change = Math.round(change * 100) / 100f;
        if (progressBarPaint != null) {
            progressBarPaint.setColor((change == 100f) ? barPositiveColor : barNegativeColor);
        }
    }

    private void invalidateAndRequest() {
        invalidate();
        requestLayout();
    }
}
