package com.example.android.bakeme.utils;

import android.content.Context;
import android.database.Cursor;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.db.RecipeContract.IngredientsEntry;
import com.example.android.bakeme.data.db.RecipeContract.RecipeEntry;

import java.util.ArrayList;

public class WidgetUtils {

    public static ArrayList<Recipe> getFavouritedRecipes(Context ctxt) {
        ArrayList<Recipe> favouritedRecipeList = new ArrayList<>();

        String selection = RecipeEntry.RECIPE_FAVOURITED + "=?";
        String[] selectionArgs = new String[]{String.valueOf(RecipeEntry.FAVOURITED_TRUE)};

        Cursor csr = ctxt.getContentResolver().query(RecipeEntry.CONTENT_URI_RECIPE,
                null, selection, selectionArgs, null);
        if (csr == null) {
            favouritedRecipeList = null;
        } else {
            csr.moveToFirst();
            while (csr.moveToNext()) {
                RecipeUtils.getRecipeList(csr, favouritedRecipeList);
            }
        }
        return favouritedRecipeList;
    }

    public static ArrayList<Ingredients> getCheckedIngredients(Context ctxt) {
        ArrayList<Ingredients> checkedIngredientsList = new ArrayList<>();

        String selection = IngredientsEntry.INGREDIENTS_CHECKED + "=?";
        String[] selectionArgs = new String[]{String.valueOf(IngredientsEntry.CHECKED_TRUE)};

        Cursor csr = ctxt.getContentResolver().query(IngredientsEntry.CONTENT_URI_INGREDIENTS,
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
