package com.kabirkang.habitgrove.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by kabirkang on 9/25/17.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {
    private static final String TAG = "DetailWidgetRemoteViews";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        return (new ListProvider(this, intent));
    }
}
