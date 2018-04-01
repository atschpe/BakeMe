package com.example.android.bakeme.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;

import java.util.List;

import static com.example.android.bakeme.data.Recipe.*;
import static com.example.android.bakeme.data.Recipe.Ingredients.INGREDIENTS_ASSOCIATED_RECIPE;
import static com.example.android.bakeme.data.Recipe.Ingredients.TABLE_INGREDIENTS;
import static com.example.android.bakeme.data.Recipe.Steps.STEPS_ASSOCIATED_RECIPE;
import static com.example.android.bakeme.data.Recipe.Steps.TABLE_STEPS;

/**
 * {@link RecipeDao} provides the CRUD methods ready for use for the ContentProvider.
 * See RoomWithContentProvider Code sample provided via AndroidStudio
 */
@Dao
public interface RecipeDao {

    // ------------- C ----------- //

    @Insert
    long[] insertAll(List<Recipe> recipes);

    /**
     * Insert recipes from api
     *
     * @param recipe the single recipe that will be added
     * @return number of recipes added.
     */
    @Insert(onConflict = 1)
    long insertRecipe(Recipe recipe);

    /**
     * insert ingredients from api
     *
     * @param ingredient the single ingredient that will be added
     * @return number of ingredients added.
     */
    @Insert(onConflict = 1)
    long insertIngredient(Ingredients ingredient);

    /**
     * insert steps from api
     *
     * @param step the single step that will be added
     * @return number of steps added.
     */
    @Insert(onConflict = 1)
    long insertStep(Steps step);

    //single inserts is not needed as we will not be writing our own.

    // ------------- R ----------- //

    /**
     * Query all recipes to display
     *
     * @return cursor with all recipes stored in the db
     */
    @Query("SELECT * FROM " + TABLE_RECIPE)
    Cursor QueryAllRecipes();

    /**
     * Query all recipes to ingredients
     *
     * @return cursor with all ingredients stored in the db
     */
    @Query("SELECT * FROM " + TABLE_INGREDIENTS + " WHERE " + INGREDIENTS_ASSOCIATED_RECIPE + " = :recipeName")
    Cursor QueryAllIngredients(String recipeName);

    /**
     * Query all steps to display
     *
     * @return cursor with all steps stored in the db
     */
    @Query("SELECT * FROM " + TABLE_STEPS + " WHERE " + STEPS_ASSOCIATED_RECIPE + "= :recipeName")
    Cursor QueryAllSteps(String recipeName);

    //we don't need to query single recipes, ingredients or steps

    // ------------- U ----------- //

    /** update recipe will allow users to favourite a recipe
     *
     * @param recipe is the recipe to be updated
     * @return number of recipes that have been updated.
     */
    @Update
    int updateRecipe(Recipe recipe);

    /** update ingredient will allow users to make a shopping list for a recipe.
     *
     * @param ingredient is the ingredient to be updated
     * @return number of ingredients that have been updated.
     */
    @Update
    int updateIngredient(Ingredients ingredient);

    // we don't need to update step information

    //mass update is not needed

    // ------------- D ----------- //
    //We do not need a delete function as we are not editing the recipes provided by the api, nor
    // will this database be accessed by other apps.
}
