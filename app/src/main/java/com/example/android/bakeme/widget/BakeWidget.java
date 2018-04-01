package com.example.android.bakeme.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakeme.R;
import com.example.android.bakeme.ui.DetailActivity;
import com.example.android.bakeme.ui.MainActivity;
import com.example.android.bakeme.utils.WidgetUtils;

/**
 * Implementation of App Widget functionality.
 */
public class BakeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context ctxt, AppWidgetManager appWidgetManager, int appWidgetId) {

        //has user favourited any recipes?
        boolean nothingFavourited = WidgetUtils.getFavouritedRecipes(ctxt).size() == 0;

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(ctxt.getPackageName(), R.layout.bake_widget);

        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent listServiceIntent = new Intent(ctxt, ListWidgetService.class);
        views.setRemoteAdapter(R.id.bakewidget_ingredientList, listServiceIntent);

        // handle clicks to open the correct BakeMe activity.
        Intent appIntent;
        if (nothingFavourited) {
            //Open MainActivity when clicked
           appIntent = new Intent(ctxt, MainActivity.class);
        } else {
            // Open DetailActivity pointing to the overview of the selected recipe when clicked
            appIntent = new Intent(ctxt, DetailActivity.class);
            appIntent.putExtra(String.valueOf(R.string.SELECTED_RECIPE),
                    WidgetUtils.getFavouritedRecipes(ctxt));
        }
        PendingIntent appPendingIntent = PendingIntent.getActivity(ctxt, 0,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.bakewidget_ingredientList, appPendingIntent);

        // Handle empty shopping list
        String emptyText;
        if (nothingFavourited) {
            emptyText = ctxt.getString(R.string.widget_no_recipes);
        } else {
            emptyText = ctxt.getString(R.string.widget_all_available);
        }
        views.setTextViewText(R.id.empty_tv, emptyText);
        views.setEmptyView(R.id.bakewidget_ingredientList, R.id.shopping_list_empty);

        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.bakewidget_ingredientList);
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

