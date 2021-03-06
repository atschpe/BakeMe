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

public class IngredientsService extends IntentService {

    final static String ACTION_REMOVE_INGREDIENTS = "com.example.android.bakeme.action.remove_ingredient";
    private final static String ACTION_UPDATE_WIDGET = "com.example.android.bakeme.action.update_widget";

    private final static String INGREDIENTS_ID = "ingredients_id";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public IngredientsService() {
        super("IngredientsService");
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
                if (getData != null && getData.containsKey(ListWidgetService.EXTRA_INGREDIENT) &&
                getData.containsKey(ListWidgetService.EXTRA_ID)) {
                    Ingredients currentIngredients = getData.getParcelable(
                            ListWidgetService.EXTRA_INGREDIENT);
                    long ingredientId = getData.getLong(ListWidgetService.EXTRA_ID);
                    handleActionRemoveIngredients(currentIngredients, ingredientId);
                }
            }
            if (action.equals(ACTION_UPDATE_WIDGET)) {
                handleActionUpdateWidget();
            }
        }
    }

    // Set checked to '0' and update the ingredient in the db. Updating the widget will then make it
    // disappear from the list.
    private void handleActionRemoveIngredients(Ingredients currentIngredients, long ingredientId) {
        currentIngredients.setChecked(false);
        currentIngredients.setId(ingredientId);
        RecipeUtils.updateCheckedDb(currentIngredients,
                this);

        startHandleActionUpdateWidget(this);
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
