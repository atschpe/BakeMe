package com.example.android.bakeme.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.data.db.RecipeProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.example.android.bakeme.data.Recipe.RECIPE_FAVOURITED;
import static com.example.android.bakeme.data.Recipe.RECIPE_ID;
import static com.example.android.bakeme.data.Recipe.RECIPE_IMAGE;
import static com.example.android.bakeme.data.Recipe.RECIPE_NAME;
import static com.example.android.bakeme.data.Recipe.RECIPE_SERVINGS;

/**
 * Methods enabling the storing of the recipe information.
 */
public class RecipeUtils {

    //Loader constants
    public static final int RECIPE_DETAIL_LOADER = 21;
    public static final int INGREDIENTS_DETAIL_LOADER = 22;
    public static final int STEPS_DETAIL_LOADER = 23;
    public static int RECIPE_MAIN_LOADER = 11;

    //Flag constants
    public static int FAV_UPDATED_FLAG = 222;
    public static int FAV_UNCHANGED_FLAG = 111;

    private static boolean favIsUpdated;

    public static boolean isFavIsUpdated() {
        return favIsUpdated;
    }

    public static void setFavIsUpdated(boolean favIsUpdated) {
        RecipeUtils.favIsUpdated = favIsUpdated;
    }

    public static String getCurrentRecipeName() {
        return currentRecipeName;
    }

    public static void setCurrentRecipeName(String currentRecipeName) {
        RecipeUtils.currentRecipeName = currentRecipeName;
    }

    static String currentRecipeName;

    public static void writeRecipesToRoom(List<Recipe> recipes, Context ctxt) {
        ContentValues singleRecipe = new ContentValues();

        for (int i = 0; i < recipes.size(); i++) {
            Recipe receivedRecipe = recipes.get(i);

            getRecipeValues(singleRecipe, receivedRecipe);

            ctxt.getContentResolver().insert(RecipeProvider.CONTENT_URI_RECIPE, singleRecipe);
        }
    }

    public static void getRecipeValues(ContentValues singleRecipe, Recipe receivedRecipe) {
        singleRecipe.put(Recipe.RECIPE_ID, receivedRecipe.getId());
        singleRecipe.put(Recipe.RECIPE_IMAGE, receivedRecipe.getImage());
        singleRecipe.put(Recipe.RECIPE_NAME, receivedRecipe.getName());
        singleRecipe.put(Recipe.RECIPE_SERVINGS, receivedRecipe.getServings());
        singleRecipe.put(Recipe.RECIPE_FAVOURITED, receivedRecipe.isFavourited());
    }

    public static void writeIngredientsToRoom(ArrayList<Ingredients> ingredientsList,
                                              String recipeName, Context ctxt) {
        ContentValues setOfIngredients = new ContentValues();

        for (int i = 0; i < ingredientsList.size(); i++) {
            Ingredients receivedIngredients = ingredientsList.get(i);

            getIngredientValues(recipeName, setOfIngredients, receivedIngredients);

            ctxt.getContentResolver().insert(RecipeProvider.CONTENT_URI_INGREDIENTS,
                    setOfIngredients);
        }
    }

    private static void getIngredientValues(String recipeName, ContentValues setOfIngredients,
                                            Ingredients receivedIngredients) {
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
                recipeName);
    }

    public static void writeStepsToRoom(ArrayList<Steps> stepsList, String recipeName,
                                        Context ctxt) {
        ContentValues setOfSteps = new ContentValues();

        for (int i = 0; i < stepsList.size(); i++) {
            Steps receivedSteps = stepsList.get(i);

            setOfSteps.put(Steps.STEPS_ID, receivedSteps.getId());
            setOfSteps.put(Steps.STEPS_THUMB, receivedSteps.getThumbnail());
            setOfSteps.put(Steps.STEPS_VIDEO, receivedSteps.getVideo());
            setOfSteps.put(Steps.STEPS_SHORT_DESCRIP,
                    receivedSteps.getShortDescription());
            setOfSteps.put(Steps.STEPS_DESCRIP, receivedSteps.getDescription());
            setOfSteps.put(Steps.STEPS_ASSOCIATED_RECIPE, recipeName);

            Uri inserted = ctxt.getContentResolver().insert(RecipeProvider.CONTENT_URI_STEPS,
                    setOfSteps);
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

    public static void updateCheckedDb(String recipeName, Ingredients selectedIngredients, Context ctxt) {
        //create uri referencing the ingredient's id
        Uri uri = ContentUris.withAppendedId(RecipeProvider.CONTENT_URI_INGREDIENTS,
                selectedIngredients.getId());

        //store changed checked state to the db.
        ContentValues singleIngredients = new ContentValues();
        getIngredientValues(recipeName, singleIngredients, selectedIngredients);
        ctxt.getContentResolver().update(uri, singleIngredients, null, null);
    }

    public static ArrayList<Ingredients> getIngredientList(Cursor data,
                                                           ArrayList<Ingredients> ingredientsList) {
        data.moveToFirst();
        while (data.moveToNext()) {
                long id = data.getLong(data.getColumnIndex(Ingredients.INGREDIENTS_ID));
                String ingredient = data.getString(data.getColumnIndex(Ingredients
                        .INGREDIENTS_INGREDIENT));
                String measure = data.getString(data.getColumnIndex(Ingredients
                        .INGREDIENTS_MEASURE));
                int quantity = data.getInt(data.getColumnIndex(Ingredients
                        .INGREDIENTS_QUANTITY));
                boolean checked = data.getInt(data.getColumnIndex(Ingredients
                        .INGREDIENTS_CHECKED)) != 0;
                ingredientsList.add(new Ingredients(id, ingredient, measure, quantity,
                        checked));
        }
        return ingredientsList;
    }

    public static ArrayList<Steps> getSteps(Cursor data, ArrayList<Steps> stepsList) {
        data.moveToFirst();
        while (data.moveToNext()) {
                long id = data.getLong(data.getColumnIndex(Steps.STEPS_ID));
                String shortDescrip
                        = data.getString(data.getColumnIndex(Steps.STEPS_SHORT_DESCRIP));
                String descrip
                        = data.getString(data.getColumnIndex(Steps.STEPS_DESCRIP));
                String video = data.getString(data.getColumnIndex(Steps.STEPS_VIDEO));
                String thumb = data.getString(data.getColumnIndex(Steps.STEPS_THUMB));
                stepsList.add(new Steps(id, shortDescrip, descrip, video, thumb));
        }
        return stepsList;
    }

    public static void getRecipeDataFromCursor(Cursor data, ArrayList<Recipe> recipeList) {
        while (data.moveToNext()) {
            int id = data.getInt(data.getColumnIndex(RECIPE_ID));
            String image = data.getString((data.getColumnIndex(RECIPE_IMAGE)));
            String name = data.getString(data.getColumnIndex(RECIPE_NAME));
            int servings = data.getInt(data.getColumnIndex(RECIPE_SERVINGS));
            boolean favourited = data.getInt(data.getColumnIndex(RECIPE_FAVOURITED)) != 0;
            recipeList.add(new Recipe(id, image, name, servings, favourited));
        }
    }
}