package com.example.android.bakeme.data.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.data.db.RecipeContract.IngredientsEntry;
import com.example.android.bakeme.data.db.RecipeContract.RecipeEntry;
import com.example.android.bakeme.data.db.RecipeContract.StepsEntry;
import com.example.android.bakeme.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * {@link RecipeProvider} is a {@link ContentProvider} communicating between the acitivities and the
 * db.
 */
public class RecipeProvider extends ContentProvider {

    private static final int RECIPE_LIST = 100; //match code for all recipes
    private static final int RECIPE_ENTRY = 101; // match code for one recipe

    private static final int STEPS_LIST = 200; // match code for all steps
    private static final int STEPS_ENTRY = 201; // match code for one step

    private static final int INGREDIENTS_LIST = 300; // match code for all ingredients
    private static final int INGREDIENTS_ENTRY = 301; // match code for one ingredient

    //private RecipeDao recipeDao;
    private SQLiteDatabase dbReader, dbWriter;

    private static UriMatcher buildMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipeContract.CONTENT_AUTH;

        uriMatcher.addURI(authority, RecipeContract.PATH_RECIPE, RECIPE_LIST);
        uriMatcher.addURI(authority, RecipeContract.PATH_RECIPE + "/#", RECIPE_ENTRY);
        uriMatcher.addURI(authority, RecipeContract.PATH_STEPS, STEPS_LIST);
        uriMatcher.addURI(authority, RecipeContract.PATH_STEPS + "/#", STEPS_ENTRY);
        uriMatcher.addURI(authority, RecipeContract.PATH_INGREDIENTS, INGREDIENTS_LIST);
        uriMatcher.addURI(authority, RecipeContract.PATH_INGREDIENTS + "/#", INGREDIENTS_ENTRY);
        return uriMatcher;
    }

    private static final UriMatcher uriMatcher = buildMatcher();

    private int getMatch(@NonNull Uri uri) {
        return uriMatcher.match(uri);
    }

    @Override
    public boolean onCreate() {
        RecipeDbHelper recipeDbHelp = new RecipeDbHelper(getContext());

        dbReader = recipeDbHelp.getReadableDatabase();
        dbWriter = recipeDbHelp.getWritableDatabase();
        Timber.plant(new Timber.DebugTree());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor csr = null;
        switch (getMatch(uri)) {
            case RECIPE_LIST:
                csr = dbReader.query(RecipeEntry.TABLE_RECIPE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INGREDIENTS_LIST:
                csr = dbReader.query(IngredientsEntry.TABLE_INGREDIENTS, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case STEPS_LIST:
                csr = dbReader.query(StepsEntry.TABLE_STEPS, projection, selection, selectionArgs,
                        null, null, sortOrder);
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
                return RecipeEntry.CONTENT_LIST_TYPE_RECIPE;
            case RECIPE_ENTRY:
                return RecipeEntry.CONTENT_ITEM_TYPE_RECIPE;
            case STEPS_LIST:
                return StepsEntry.CONTENT_LIST_TYPE_STEPS;
            case STEPS_ENTRY:
                return StepsEntry.CONTENT_ITEM_TYPE_STEPS;
            case INGREDIENTS_LIST:
                return IngredientsEntry.CONTENT_LIST_TYPE_INGREDIENTS;
            case INGREDIENTS_ENTRY:
                return IngredientsEntry.CONTENT_ITEM_TYPE_INGREDIENTS;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) { // no single insertRecipe needed
        int match = getMatch(uri);
        assert values != null;
        switch (match) {
            case RECIPE_LIST:
                long recipeId = dbWriter.insert(RecipeEntry.TABLE_RECIPE, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, recipeId);
            case INGREDIENTS_LIST:
                long ingredientsId = dbWriter.insert(IngredientsEntry.TABLE_INGREDIENTS,
                        null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, ingredientsId);
            case STEPS_LIST:
                long stepsId = dbWriter.insert(StepsEntry.TABLE_STEPS, null, values);
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
        switch (match) { //we only need to update single recipe and ingredient items
            case RECIPE_ENTRY:
                int countRecipe = dbWriter.update(RecipeEntry.TABLE_RECIPE, values, selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                Timber.v("recipe update: " + countRecipe);
                return countRecipe;
            case INGREDIENTS_ENTRY:
                int countIngredient = dbWriter.update(IngredientsEntry.TABLE_INGREDIENTS, values,
                        selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return countIngredient;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}