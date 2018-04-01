package com.example.android.bakeme.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.utils.RecipeUtils;

import timber.log.Timber;

public class IngredientsService extends IntentService {

    final static String ACTION_REMOVE_INGREDIENTS = "com.example.android.bakeme.action.remove_ingredient";
    final static String ACTION_UPDATE_WIDGET = "com.example.android.bakeme.action.update_widget";

    final static String INGREDIENTS_ID = "ingredients_id";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public IngredientsService() {
        super("IngredientsService");
    }

    public static void startHandleRemoveIngredients(Context ctxt, long ingredientId) {
        Intent removalIntent = new Intent(ctxt, IngredientsService.class);
        removalIntent.setAction(ACTION_REMOVE_INGREDIENTS);
        removalIntent.putExtra(INGREDIENTS_ID, ingredientId);
        ctxt.startService(removalIntent);
    }

    public static void startHandleActionUpdateWidget(Context ctxt) {
        Intent updateIntent = new Intent(ctxt, IngredientsService.class);
        updateIntent.setAction(ACTION_UPDATE_WIDGET);
        ctxt.startService(updateIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_REMOVE_INGREDIENTS)) {
                Bundle getData = intent.getExtras();
                Ingredients currentIngredients = null;
                if (getData != null && getData.containsKey(ListWidgetService.EXTRA_ID)) {
                    currentIngredients = getData.getParcelable(ListWidgetService.EXTRA_INGREDIENT);
                }
                if (currentIngredients != null) {
                handleActionRemoveIngredients(currentIngredients);}
            } if (action.equals(ACTION_UPDATE_WIDGET)) {
                handleActionUpdateWidget();
            }
        }
    }

    //set checked to '0'
    private void handleActionRemoveIngredients(Ingredients currentIngredients) {
        currentIngredients.setChecked(false);
        RecipeUtils.updateCheckedDb(currentIngredients.getAssociatedRecipe(), currentIngredients,
                getBaseContext());

        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetMan.getAppWidgetIds(new ComponentName(this,
                BakeWidget.class));
        //update data for listView
        appWidgetMan.notifyAppWidgetViewDataChanged(appWidgetIds,
                R.id.bakewidget_ingredientList);
        //Now update all widgets
        BakeWidget.updateBakeWidgets(this, appWidgetMan, appWidgetIds);

    }

    private void handleActionUpdateWidget() {
        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetMan.getAppWidgetIds(new ComponentName(this,
                BakeWidget.class));
        //Now update all widgets
        BakeWidget.updateBakeWidgets(this, appWidgetMan, appWidgetIds);
        //update data for listView
        appWidgetMan.notifyAppWidgetViewDataChanged(appWidgetIds,
                R.id.bakewidget_ingredientList);

    }
}
