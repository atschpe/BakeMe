package com.example.android.bakeme.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.db.RecipeContract.IngredientsEntry;

import java.util.ArrayList;

import timber.log.Timber;

public class ListWidgetService extends RemoteViewsService {
    public static String EXTRA_ID = "extra_id";
    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_INGREDIENT = "extra_pojo";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext());
    }

    private class ListRemoteViewFactory implements RemoteViewsFactory {

        Context ctxt;
        Cursor csr;
        ArrayList<Ingredients> ingredientsList;

        public ListRemoteViewFactory(Context applicationContext) {
            this.ctxt = applicationContext;
        }

        @Override
        public void onCreate() {
            ingredientsList = new ArrayList<>();
            Timber.plant(new Timber.DebugTree());
        }

        //Called when first launched and on any update made from within the app or widget
        @Override
        public void onDataSetChanged() {
            String selection = IngredientsEntry.INGREDIENTS_CHECKED + "=?";
            String[] selectionArgs = new String[]{String.valueOf(IngredientsEntry.CHECKED_TRUE)};
            String sortOrder = IngredientsEntry.INGREDIENTS_ASSOCIATED_RECIPE;
            csr = ctxt.getContentResolver().query(IngredientsEntry.CONTENT_URI_INGREDIENTS,
                    null, selection, selectionArgs, sortOrder);
        }

        @Override
        public void onDestroy() {
            csr.close();
        }

        @Override
        public int getCount() {
            if (csr == null) return 0;
            return csr.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Timber.v("getAtView called");
            if (csr == null || csr.getCount() == 0) return null;

            RemoteViews views = new RemoteViews(ctxt.getPackageName(),
                    R.layout.widget_ingredient_item);
            csr.moveToPosition(position);
            String recipeName
                    = csr.getString(csr.getColumnIndex(IngredientsEntry.INGREDIENTS_ASSOCIATED_RECIPE));
            long id = csr.getLong(csr.getColumnIndex(IngredientsEntry.INGREDIENTS_ID));
            String ingredient
                    = csr.getString(csr.getColumnIndex(IngredientsEntry.INGREDIENTS_INGREDIENT));
            String measure
                    = csr.getString(csr.getColumnIndex(IngredientsEntry.INGREDIENTS_MEASURE));
            double quantity
                    = csr.getDouble(csr.getColumnIndex(IngredientsEntry.INGREDIENTS_QUANTITY));

            //set data to the Ingredient POJO, so to use the toString() method.
            Ingredients currentIngredient = new Ingredients(id, ingredient, measure, quantity,
                    recipeName);

            views.setTextViewText(R.id.widget_ingredient_tv, currentIngredient.toString());

            // Fill in the onClick PendingIntent Template using the Id for each item individually
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_INGREDIENT, currentIngredient);
            extras.putLong(EXTRA_ID, id);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);

            //Preparing all data for the click event
            views.setOnClickFillInIntent(R.id.widget_ingredient_tv, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        //all the same type
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
