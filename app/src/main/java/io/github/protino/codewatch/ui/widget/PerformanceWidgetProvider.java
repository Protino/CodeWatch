package io.github.protino.codewatch.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.github.protino.codewatch.utils.Constants;

/**
 * @author Gurupad Mamadapur
 */

public class PerformanceWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context,PerformanceWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context,PerformanceWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(Constants.ACTION_DATA_UPDATED.equals(intent.getAction())){
            context.startService(new Intent(context,PerformanceWidgetIntentService.class));
        }
    }
}
