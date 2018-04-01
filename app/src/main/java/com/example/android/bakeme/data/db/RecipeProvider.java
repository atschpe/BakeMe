package com.example.android.bakeme.data.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * {@link RecipeProvider} is a {@link ContentProvider} communicating between the acitivities and the
 * db using {@link RecipeDao}.
 */
public class RecipeProvider extends ContentProvider {

    //authority & uri
    public static final String CONTENT_AUTH = "com.example.android.bakeme";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);

    //paths for the tables
    public static final String PATH_RECIPE = "recipe";
    public static final String PATH_STEPS = "steps";
    public static final String PATH_INGREDIENTS = "ingredients";

    // table uris
    public static final Uri CONTENT_URI_RECIPE = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_RECIPE).build();
    public static final Uri CONTENT_URI_STEPS = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_STEPS).build();
    public static final Uri CONTENT_URI_INGREDIENTS = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_INGREDIENTS).build();

    //MIME types
    public static final String CONTENT_LIST_TYPE_RECIPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + CONTENT_AUTH + PATH_RECIPE;
    public static final String CONTENT_ITEM_TYPE_RECIPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + CONTENT_AUTH + PATH_RECIPE;
    public static final String CONTENT_LIST_TYPE_STEPS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + CONTENT_AUTH + PATH_STEPS;
    public static final String CONTENT_ITEM_TYPE_STEPS = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + CONTENT_AUTH + PATH_STEPS;
    public static final String CONTENT_LIST_TYPE_INGREDIENTS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + CONTENT_AUTH + PATH_INGREDIENTS;
    public static final String CONTENT_ITEM_TYPE_INGREDIENTS = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + CONTENT_AUTH + PATH_INGREDIENTS;

    private static final int RECIPE_LIST = 100; //match code for all recipes
    private static final int RECIPE_ENTRY = 101; // match code for one recipe

    private static final int STEPS_LIST = 200; // match code for all steps
    private static final int STEPS_ENTRY = 201; // match code for one step

    private static final int INGREDIENTS_LIST = 300; // match code for all ingredients
    private static final int INGREDIENTS_ENTRY = 301; // match code for one ingredient

    private RecipeDao recipeDao;

    private static UriMatcher buildMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTH;

        uriMatcher.addURI(authority, PATH_RECIPE, RECIPE_LIST);
        uriMatcher.addURI(authority, PATH_RECIPE + "/#", RECIPE_ENTRY);
        uriMatcher.addURI(authority, PATH_STEPS, STEPS_LIST);
        uriMatcher.addURI(authority, PATH_STEPS + "/#", STEPS_ENTRY);
        uriMatcher.addURI(authority, PATH_INGREDIENTS, INGREDIENTS_LIST);
        uriMatcher.addURI(authority, PATH_INGREDIENTS + "/#", INGREDIENTS_ENTRY);
        return uriMatcher;
    }

    private static final UriMatcher uriMatcher = buildMatcher();

    private int getMatch(@NonNull Uri uri) {
        return uriMatcher.match(uri);
    }

    @Override
    public boolean onCreate() {
        recipeDao = RecipeDatabase.getRecipeDbInstance(getContext()).recipeDao();
        Timber.plant(new Timber.DebugTree());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor csr;
        switch (getMatch(uri)) {
            case RECIPE_LIST:
                csr = recipeDao.QueryAllRecipes();
                break;
            case INGREDIENTS_LIST:
                csr = recipeDao.QueryAllIngredients(RecipeUtils.getCurrentRecipeName());
                break;
            case STEPS_LIST:
                csr = recipeDao.QueryAllSteps(RecipeUtils.getCurrentRecipeName());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri, which cannot be queried: " + uri);
        }
        csr.setNotificationUri(getContext().getContentResolver(), uri);
        return csr;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = getMatch(uri);
        switch (match) {
            case RECIPE_LIST:
                return CONTENT_LIST_TYPE_RECIPE;
            case RECIPE_ENTRY:
                return CONTENT_ITEM_TYPE_RECIPE;
            case STEPS_LIST:
                return CONTENT_LIST_TYPE_STEPS;
            case STEPS_ENTRY:
                return CONTENT_ITEM_TYPE_STEPS;
            case INGREDIENTS_LIST:
                return CONTENT_LIST_TYPE_INGREDIENTS;
            case INGREDIENTS_ENTRY:
                return CONTENT_ITEM_TYPE_INGREDIENTS;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri + " with match " + match);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (getMatch(uri)) {
            case RECIPE_LIST:
                final List<Recipe> recipes = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    recipes.set(i, Recipe.fromContentValues(values[i]));
                }
                return recipeDao.insertAll(recipes).length;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) { // no single insertRecipe needed
        int match = getMatch(uri);
        assert values != null;
        switch (match) {
            case RECIPE_LIST:
                long recipeId = recipeDao.insertRecipe(Recipe.fromContentValues(values));
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, recipeId);
            case INGREDIENTS_LIST:
                long ingredientsId = recipeDao.insertIngredient(Ingredients.fromContentValues(values));
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, ingredientsId);
            case STEPS_LIST:
                long stepsId = recipeDao.insertStep(Steps.fromContentValues(values));
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, stepsId);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0; //no deletes needed
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        int match = getMatch(uri);
        switch (match) { //we only need to update single items
            case RECIPE_ENTRY:
                Recipe recipe = Recipe.fromContentValues(values);
                int countRecipe = recipeDao.updateRecipe(recipe);
                getContext().getContentResolver().notifyChange(uri, null);
                Timber.v("recipe update: " + countRecipe);
                return countRecipe;
            case INGREDIENTS_ENTRY:
                Ingredients ingredient = Ingredients.fromContentValues(values);
                int countIngredient = recipeDao.updateIngredient(ingredient);
                getContext().getContentResolver().notifyChange(uri, null);
                return countIngredient;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}