package com.example.android.bakeme.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.ui.DetailActivity;
import com.example.android.bakeme.ui.MainActivity;
import com.example.android.bakeme.utils.RecipeUtils;
import com.example.android.bakeme.utils.WidgetUtils;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class BakeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context ctxt, AppWidgetManager appWidgetManager, int appWidgetId) {

        Timber.plant(new Timber.DebugTree());
        Timber.v("Timber is working within the widget classes");

        //has user favourited any recipes?
        boolean nothingFavourited = WidgetUtils.getFavouritedRecipes(ctxt).size() == 0;
        boolean nothingChecked = WidgetUtils.getCheckedIngredients(ctxt).size() == 0;

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(ctxt.getPackageName(), R.layout.bake_widget);

        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent listServiceIntent = new Intent(ctxt, ListWidgetService.class);
        views.setRemoteAdapter(R.id.bakewidget_ingredientList, listServiceIntent);

        // Handle empty shopping list
        String emptyText;
        if (nothingFavourited) {
            emptyText = ctxt.getString(R.string.widget_no_recipes);
        } else {
            emptyText = ctxt.getString(R.string.widget_all_available);
        }
        views.setTextViewText(R.id.empty_tv, emptyText);
        views.setEmptyView(R.id.bakewidget_ingredientList, R.id.shopping_list_empty);

        Intent intent;
        int requestCode = 0;
        if (nothingFavourited) {
            //Open MainActivity when clicked
            intent = new Intent(ctxt, MainActivity.class);
            requestCode = RecipeUtils.FAV_UNCHANGED_FLAG;
        } else if (nothingChecked) {
            // Open DetailActivity pointing to the first overview of the selected recipe when clicked
            intent = new Intent(ctxt, DetailActivity.class);
            intent.putExtra(String.valueOf(R.string.SELECTED_RECIPE),
                    WidgetUtils.getFavouritedRecipes(ctxt).get(0));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(ctxt, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.bakewidget_ingredientList, pendingIntent);

        // handle clicks to open the correct BakeMe activity.
        if (!nothingFavourited && !nothingChecked) {
            // Set the PlantDetailActivity intent to launch when clicked
            intent = new Intent(ctxt, DetailActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(ctxt, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.bakewidget_ingredientList, appPendingIntent);
        }

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

