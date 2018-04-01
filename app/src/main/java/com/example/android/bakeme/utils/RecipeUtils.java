package com.example.android.bakeme.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.data.db.RecipeProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Methods enabling the storing of the recipe information.
 */
public class RecipeUtils {

    //Loader constants
    public static final int RECIPE_DETAIL_LOADER = 21;
    public static final int INGREDIENTS_DETAIL_LOADER = 22;
    public static final int STEPS_DETAIL_LOADER = 23;
    public static int RECIPE_MAIN_LOADER = 11;

    public static long getCurrentRecipeId() {
        return currentRecipeId;
    }

    public static void setCurrentRecipeId(long currentRecipeId) {
        RecipeUtils.currentRecipeId = currentRecipeId;
    }

    static long currentRecipeId;

    public static void writeRecipesToRoom(List<Recipe> recipes, Context ctxt) {
        ContentValues singleRecipe = new ContentValues();

        for (int i = 0; i < recipes.size(); i++) {
            Recipe receivedRecipe = recipes.get(i);

            getRecipeValues(singleRecipe, receivedRecipe);

            ctxt.getContentResolver().insert(RecipeProvider.CONTENT_URI_RECIPE, singleRecipe);
        }
    }

    private static void getRecipeValues(ContentValues singleRecipe, Recipe receivedRecipe) {
        singleRecipe.put(Recipe.RECIPE_ID, receivedRecipe.getId());
        singleRecipe.put(Recipe.RECIPE_IMAGE, receivedRecipe.getImage());
        singleRecipe.put(Recipe.RECIPE_NAME, receivedRecipe.getName());
        singleRecipe.put(Recipe.RECIPE_SERVINGS, receivedRecipe.getServings());
        singleRecipe.put(Recipe.RECIPE_FAVOURITED, receivedRecipe.isFavourited());
    }

    public static void writeIngredientsToRoom(ArrayList<Ingredients> ingredientsList,
                                              long recipeId, Context ctxt) {
        ContentValues setOfIngredients = new ContentValues();

        for (int i = 0; i < ingredientsList.size(); i++) {
            Ingredients receivedIngredients = ingredientsList.get(i);

            setOfIngredients.put(Ingredients.INGREDIENTS_ID, receivedIngredients.getId());
            setOfIngredients.put(Ingredients.INGREDIENTS_INGREDIENT, receivedIngredients
                    .getIngredient());
            setOfIngredients.put(Ingredients.INGREDIENTS_MEASURE,
                    receivedIngredients.getMeasure());
            setOfIngredients.put(Ingredients.INGREDIENTS_QUANTITY, receivedIngredients
                    .getQuantity());
            setOfIngredients.put(Ingredients.INGREDIENTS_CHECKED,
                    receivedIngredients.isChecked());
            setOfIngredients.put(Ingredients.INGREDIENTS_ASSOCIATED_RECIPE,
                    recipeId);

            ctxt.getContentResolver().insert(RecipeProvider.CONTENT_URI_INGREDIENTS, setOfIngredients);
        }
    }

    public static void writeStepsToRoom(ArrayList<Steps> stepsList, long recipeId, Context ctxt) {
        ContentValues setOfSteps = new ContentValues();

        for (int i = 0; i < stepsList.size(); i++) {
            Steps receivedSteps = stepsList.get(i);

            setOfSteps.put(Steps.STEPS_ID, receivedSteps.getId());
            setOfSteps.put(Steps.STEPS_THUMB, receivedSteps.getThumbnail());
            setOfSteps.put(Steps.STEPS_VIDEO, receivedSteps.getVideo());
            setOfSteps.put(Steps.STEPS_SHORT_DESCRIP,
                    receivedSteps.getShortDescription());
            setOfSteps.put(Steps.STEPS_DESCRIP, receivedSteps.getDescription());
            setOfSteps.put(Steps.STEPS_ASSOCIATED_RECIPE, recipeId);

            Uri inserted = ctxt.getContentResolver().insert(RecipeProvider.CONTENT_URI_STEPS, setOfSteps);
            Timber.v("writing Step to room" + inserted + "; recipe id: " + recipeId);
        }
    }

    public static void updateFavDb(Recipe selectedRecipe, Context ctxt) {
        //create uri referencing the recipe's id
        Uri uri = ContentUris.withAppendedId(RecipeProvider.CONTENT_URI_RECIPE,
                selectedRecipe.getId());

        //store changed favourite selection to the db.
        ContentValues singleRecipe = new ContentValues();
        getRecipeValues(singleRecipe, selectedRecipe);
        ctxt.getContentResolver().update(uri, singleRecipe, null, null);
    }
}