package com.example.android.bakeme.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.db.RecipeProvider;

import java.util.ArrayList;

import timber.log.Timber;

public class ListWidgetService extends RemoteViewsService {
    private String EXTRA_ID = "extra_id";
    private String EXTRA_RECIPE_NAME = "extra_recipe_id";
    private String prevRecipeName;

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
            Timber.plant(new Timber.DebugTree());
        }

        //Called when first launched and on any update made from within the app or widget
        @Override
        public void onDataSetChanged() {
            if (csr != null) csr.close();
            String selection = Ingredients.INGREDIENTS_CHECKED + "=?";
            String[] selectionArgs = new String[]{"1"};
            csr = getContentResolver().query(RecipeProvider.CONTENT_URI_INGREDIENTS, null,
                    selection, selectionArgs, Ingredients.INGREDIENTS_ASSOCIATED_RECIPE);
            Timber.v(String.valueOf(csr));
            Timber.v(String.valueOf(csr.getCount()));
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
            Timber.v("getViewAt() is called");
            RemoteViews views = new RemoteViews(ctxt.getPackageName(), R.layout.widget_ingredient_item);
            if (csr != null && csr.getCount() > 0) {
                csr.moveToPosition(position);
                String recipeName = csr.getString(csr.getColumnIndex(Ingredients.INGREDIENTS_ASSOCIATED_RECIPE));
                long id = csr.getLong(csr.getColumnIndex(Ingredients.INGREDIENTS_ID));
                String ingredient = csr.getString(csr.getColumnIndex(Ingredients.INGREDIENTS_INGREDIENT));
                String measure = csr.getString(csr.getColumnIndex(Ingredients.INGREDIENTS_MEASURE));
                double quantity = csr.getDouble(csr.getColumnIndex(Ingredients.INGREDIENTS_QUANTITY));
                ingredientsList.add(new Ingredients(id, ingredient, measure, quantity, recipeName));


                //only show the recipe name on the first ingredient in the list.
                if (recipeName.equals(prevRecipeName)) {
                    views.setViewVisibility(R.id.widget_recipe_tv, View.GONE);
                } else {
                    views.setViewVisibility(R.id.widget_recipe_tv, View.VISIBLE);
                    views.setTextViewText(R.id.widget_recipe_tv, recipeName);
                    prevRecipeName = recipeName;
                }

                views.setTextViewText(R.id.widget_ingredient_tv, ingredientsList.toString());

                // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
                Bundle extras = new Bundle();
                extras.putLong(EXTRA_ID, id);
                extras.putString(EXTRA_RECIPE_NAME, recipeName);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.widget_ingredient_tv, fillInIntent);
            }

            csr.close();

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
