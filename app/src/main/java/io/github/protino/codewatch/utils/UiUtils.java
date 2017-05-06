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

package io.github.protino.codewatch.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import io.github.protino.codewatch.R;

/**
 * Basic helper functions related to UI
 *
 * @author Gurupad Mamadapur
 */

public class UiUtils {
    /**
     * @param dp dp value
     * @return converted px value
     */
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * @param px dp value
     * @return converted dp value
     */

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * @return device screen height in pixels
     */
    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * @param src Original image
     * @param context needed to fetch resources
     * @return new bitmap resource containing the watermark
     */
    public static Bitmap addWaterMark(Bitmap src, Context context) {

        int height = src.getHeight() + UiUtils.dpToPx(32 + 8);
        int width = src.getWidth();

        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        int darkGrey = context.getResources().getColor(R.color.grey_900);
        Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        appIcon = Bitmap.createScaledBitmap(appIcon, UiUtils.dpToPx(32), UiUtils.dpToPx(32), false);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setShadowLayer(6, 2, 2, darkGrey);

        Paint textPaint = new Paint();
        textPaint.setColor(darkGrey);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(UiUtils.dpToPx(16));
        textPaint.setFakeBoldText(true);

        Paint appIconPaint = new Paint();
        appIconPaint.setAntiAlias(true);
        appIconPaint.setFilterBitmap(true);
        appIconPaint.setDither(true);

        float srcHeight = src.getHeight();
        int _4dp = UiUtils.dpToPx(4);

        String watermarkText = context.getString(R.string.app_name);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float watermarkTextY = srcHeight + (height - srcHeight) / 2f + (fontMetrics.descent - fontMetrics.ascent) / 2f;

        canvas.drawRect(0, srcHeight, width, height, backgroundPaint);
        canvas.drawText(watermarkText, (_4dp) * 2 + appIcon.getWidth(), watermarkTextY, textPaint);
        canvas.drawBitmap(appIcon, _4dp, _4dp + srcHeight, appIconPaint);

        appIcon.recycle();
        src.recycle();
        return result;
    }
}
