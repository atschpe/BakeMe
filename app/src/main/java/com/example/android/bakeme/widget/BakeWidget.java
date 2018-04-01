package com.example.android.bakeme.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.ui.DetailActivity;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class BakeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context ctxt, AppWidgetManager appWidgetManager, int appWidgetId) {

        String recipeTitle = null; //TODO: retrieve title of favourited recipe(s)
        CharSequence widgetText = String.valueOf(R.string.widget_title) + recipeTitle;
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(ctxt.getPackageName(), R.layout.bake_widget);
        views.setTextViewText(R.id.widget_title, widgetText);

        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent listServiceIntent = new Intent(ctxt, ListWidgetService.class);
        views.setRemoteAdapter(R.id.bakewidget_ingredientList, listServiceIntent);

        // Set the DetailActivity intent to launch when clicked
        Intent openDetailAppIntent = new Intent(ctxt, DetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(ctxt, 0,
                openDetailAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.shopping_list_empty, appPendingIntent);
        // Handle empty gardens
        views.setEmptyView(R.id.bakewidget_ingredientList, R.id.shopping_list_empty);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       IngredientsService.startHandleActionUpdateWidget(ctxt);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateBakeWidgets(Context ctxt, AppWidgetManager appWidgetMan,
                                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(ctxt, appWidgetMan, appWidgetId);
        }
    }
}

