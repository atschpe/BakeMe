package com.example.android.bakeme.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.db.RecipeProvider;

import java.util.ArrayList;

public class WidgetUtils {

    public static ArrayList<Long> getFavouritedRecipes(Context ctxt) {
        ArrayList<Long> favRecipeIds = new ArrayList<>();

        String selection = Recipe.RECIPE_FAVOURITED + "=?";
        String[] selectionArgs = new String[]{"1"};

        Cursor csr = ctxt.getContentResolver().query(RecipeProvider.CONTENT_URI_RECIPE,
                null, selection, selectionArgs, null);
        if (csr == null) {
            favRecipeIds = null;
        } else {
            csr.moveToFirst();
            while (csr.moveToNext()) {
                long favRecipeId = csr.getLong(csr.getColumnIndex(Recipe.RECIPE_ID));
                favRecipeIds.add(favRecipeId);
            }
        }
        return favRecipeIds;
    }

    public static ArrayList<Ingredients> getCheckedIngredients(long favRecipeId,
                                                                      Context ctxt) {
        ArrayList<Ingredients> checkedIngredientsList = new ArrayList<>();

        String selection = Ingredients.INGREDIENTS_ASSOCIATED_RECIPE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(favRecipeId)};

        Cursor csr = ctxt.getContentResolver().query(RecipeProvider.CONTENT_URI_INGREDIENTS,
                null, selection, selectionArgs, null);
        if (csr == null) {
            checkedIngredientsList = null;
        } else {
            csr.moveToFirst();
            while (csr.moveToNext()) {
                RecipeUtils.getIngredientList(csr, checkedIngredientsList);
            }
        }

        return checkedIngredientsList;
    }
}
