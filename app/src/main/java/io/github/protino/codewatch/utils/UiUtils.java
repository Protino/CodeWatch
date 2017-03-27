package io.github.protino.codewatch.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
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
}
