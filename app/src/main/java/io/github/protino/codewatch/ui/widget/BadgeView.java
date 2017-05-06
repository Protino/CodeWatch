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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.utils.UiUtils;

/**
 * ====================================================================
 * IMPORTANT
 * ====================================================================
 * Don't forget to call requestLayout and invalidate after changing the
 * properties.
 *
 * @author Gurupad Mamadapur
 */

public class BadgeView extends View {

    private static final float OUTER_RADIUS_MULTIPLIER = 1.4f; // 140%
    private int textWidth;
    private int textHeight;
    private int badgeRadius;
    private int space;
    private int textSize;

    private String text;

    private Paint backgroundPaint;
    private Paint badgePaint;
    private Paint textPaint;

    private int textColor;
    private int badgeColor;
    private int backgroundColor;

    public BadgeView(Context context) {
        super(context);
        initializeData();
        setUpPaint();
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeData();
        setUpPaint();
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeData();
        setUpPaint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeData();
        setUpPaint();
    }

    private void initializeData() {
        text = "";
        textHeight = 0;
        textWidth = 0;
        badgeRadius = UiUtils.dpToPx(16);
        space = UiUtils.dpToPx(4);
        textSize = UiUtils.dpToPx(24);

        textColor = getResources().getColor(R.color.white_1000);
        backgroundColor = getResources().getColor(R.color.grey_800);
        badgeColor = getResources().getColor(R.color.dark_bronze);
    }

    private void setUpPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgePaint.setColor(badgeColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setFakeBoldText(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int contentWidth = textWidth + (int) (badgeRadius * (3 * OUTER_RADIUS_MULTIPLIER)) + (2 * space);
        int minWidth = getPaddingLeft() + getPaddingRight() + contentWidth;
        int resolvedWidth = resolveSizeAndState(minWidth, widthMeasureSpec, 0);

        int minHeight = getPaddingBottom() + getPaddingTop() + (int) ((2 * OUTER_RADIUS_MULTIPLIER) * badgeRadius);
        int resolvedHeight = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float height = getMeasuredHeight();

        float outerRadius = badgeRadius * OUTER_RADIUS_MULTIPLIER;

        //draw background
        canvas.drawCircle(outerRadius, outerRadius, outerRadius, backgroundPaint);
        canvas.drawRect(outerRadius, 0, textWidth + (2 * space) + (2 * outerRadius), outerRadius * 2, backgroundPaint);
        canvas.drawCircle(textWidth + (2 * space) + (2 * outerRadius), outerRadius, outerRadius, backgroundPaint);

        //draw badge
        canvas.drawCircle(outerRadius, outerRadius, badgeRadius, badgePaint);

        canvas.drawText(text, (outerRadius * 2) + space, outerRadius + textPaint.descent() * 1.2f, textPaint);
    }

    public void setBadgeRadius(int badgeRadius) {
        int sizeInDp = UiUtils.dpToPx(textSize);
        this.badgeRadius = sizeInDp;
    }

    public void setText(String text) {
        this.text = text;
        measureText();
    }

    private void measureText() {
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        textWidth = rect.width();
        textHeight = rect.height();
    }

    /**
     * @param textSize in dp
     */
    public void setTextSize(int textSize) {
        int sizeInDp = UiUtils.dpToPx(textSize);
        this.textSize = sizeInDp;
        textPaint.setTextSize(sizeInDp);
        measureText();
    }

    public void setSpace(int space) {
        int sizeInDp = UiUtils.dpToPx(space);
        this.space = sizeInDp;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        backgroundPaint.setColor(backgroundColor);
        this.backgroundColor = backgroundColor;
    }

    public void setBadgeColor(int badgeColor) {
        this.badgeColor = badgeColor;
        badgePaint.setColor(badgeColor);
    }

    public void setTextColor(int textColor) {
        textPaint.setColor(textColor);
        this.textColor = textColor;
    }
}
