package com.admteal.dndhp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RemoteViews;

public class AADNDHPWidgetProvider extends AppWidgetProvider {
	public static String activityCurrentHP;
	private RemoteViews views;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {		
        final int N = appWidgetIds.length;
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            ///Intent intent = new Intent(context, DNDHPActivity.class);
            ///PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener to the button
            views = new RemoteViews(context.getPackageName(), R.layout.hp_widget_layout);
            ///views.setOnClickPendingIntent(R.id.hp_widget_view, pendingIntent);
            views.setTextViewText(R.id.widgetButton, activityCurrentHP);
            /// Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle pullOldData = intent.getExtras();
		if (pullOldData == null) {
			return;
		}
		activityCurrentHP = pullOldData.getString(context.getString(R.string.CURRENT_HP)); 
		super.onReceive(context,  intent);
	}
}