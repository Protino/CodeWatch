package io.github.protino.codewatch.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import io.github.protino.codewatch.R;


/**
 * A layout that draws something in the insets passed to {@link #fitSystemWindows(Rect)}, i.e. the area above UI chrome
 * (status and navigation bars, overlay action bars).
 *
 * @see <a href="https://goo.gl/f5jx2z">Google IoSched 2016 app</a>
 */

public class ScrimInsetsFrameLayout extends FrameLayout {
    private Drawable insetForeground;

    private Rect insets;
    private Rect tempRect = new Rect();
    private OnInsetsCallback onInsetsCallback;

    public ScrimInsetsFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ScrimInsetsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ScrimInsetsFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ScrimInsetsView, defStyle, 0);
        if (a == null) {
            return;
        }
        insetForeground = a.getDrawable(R.styleable.ScrimInsetsView_insetForeground);
        a.recycle();

        setWillNotDraw(true);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        this.insets = new Rect(insets);
        setWillNotDraw(insetForeground == null);
        ViewCompat.postInvalidateOnAnimation(this);
        if (onInsetsCallback != null) {
            onInsetsCallback.onInsetsChanged(insets);
        }
        return true; // consume insets
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (insets != null && insetForeground != null) {
            int sc = canvas.save();
            canvas.translate(getScrollX(), getScrollY());

            // Top
            tempRect.set(0, 0, width, insets.top);
            insetForeground.setBounds(tempRect);
            insetForeground.draw(canvas);

            // Bottom
            tempRect.set(0, height - insets.bottom, width, height);
            insetForeground.setBounds(tempRect);
            insetForeground.draw(canvas);

            // Left
            tempRect.set(0, insets.top, insets.left, height - insets.bottom);
            insetForeground.setBounds(tempRect);
            insetForeground.draw(canvas);

            // Right
            tempRect.set(width - insets.right, insets.top, width, height - insets.bottom);
            insetForeground.setBounds(tempRect);
            insetForeground.draw(canvas);

            canvas.restoreToCount(sc);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (insetForeground != null) {
            insetForeground.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (insetForeground != null) {
            insetForeground.setCallback(null);
        }
    }

    /**
     * Allows the calling container to specify a callback for custom processing when insets change (i.e. when
     * {@link #fitSystemWindows(Rect)} is called. This is useful for setting padding on UI elements based on
     * UI chrome insets (e.g. a Google Map or a ListView). When using with ListView or GridView, remember to set
     * clipToPadding to false.
     */
    public void setOnInsetsCallback(OnInsetsCallback onInsetsCallback) {
        this.onInsetsCallback = onInsetsCallback;
    }

    public static interface OnInsetsCallback {
        public void onInsetsChanged(Rect insets);
    }
}
