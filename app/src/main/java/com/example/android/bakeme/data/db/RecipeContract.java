package com.example.android.bakeme.data.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeContract {

    //authority & uri
    public static final String CONTENT_AUTH = "com.example.android.bakeme";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);

    //paths for the tables
    public static final String PATH_RECIPE = "recipe";
    public static final String PATH_STEPS = "steps";
    public static final String PATH_INGREDIENTS = "ingredients";

    //reused column name
    static final String ASSOCIATED_RECIPE = "associatedRecipe";

    public static final class RecipeEntry {

        // table uri
        public static final Uri CONTENT_URI_RECIPE = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE).build();

        //MIME types
        public static final String CONTENT_LIST_TYPE_RECIPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTH + PATH_RECIPE;
        public static final String CONTENT_ITEM_TYPE_RECIPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTH + PATH_RECIPE;

        //db table as well as api keys
        public static final String TABLE_RECIPE = "recipes";
        public static final String RECIPE_ID = "id";
        public static final String RECIPE_IMAGE = "image";
        public static final String RECIPE_SERVINGS = "servings";
        public static final String RECIPE_NAME = "name";
        public static final String RECIPE_FAVOURITED = "favourited";

        //favourite ints (for boolean)
        public static final int FAVOURITED_TRUE = 1;
        public static final int FAVOURITED_FALSE = 0;
    }

    public static final class IngredientsEntry implements BaseColumns {
        // table uri
        public static final Uri CONTENT_URI_INGREDIENTS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS).build();

        //MIME types
        public static final String CONTENT_LIST_TYPE_INGREDIENTS
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTH + PATH_INGREDIENTS;
        public static final String CONTENT_ITEM_TYPE_INGREDIENTS
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTH + PATH_INGREDIENTS;

        public static final String TABLE_INGREDIENTS = "ingredients";
        public static final String INGREDIENTS_ID = BaseColumns._ID;
        public static final String INGREDIENTS_INGREDIENT = "ingredient";
        public static final String INGREDIENTS_MEASURE = "measure";
        public static final String INGREDIENTS_QUANTITY = "quantity";
        public static final String INGREDIENTS_CHECKED = "checked";
        public static final String INGREDIENTS_ASSOCIATED_RECIPE = ASSOCIATED_RECIPE;

        //checked ints (for boolean)
        public static final int CHECKED_TRUE = 1;
        public static final int CHECKED_FALSE = 0;
    }

    public static final class StepsEntry implements BaseColumns {
        // table uri
        public static final Uri CONTENT_URI_STEPS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STEPS).build();

        //MIME types
        public static final String CONTENT_LIST_TYPE_STEPS = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTH + PATH_STEPS;
        public static final String CONTENT_ITEM_TYPE_STEPS = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTH + PATH_STEPS;

        public static final String TABLE_STEPS = "steps";
        public static final String STEPS_ID = "id";
        public static final String STEPS_GLOBAL_ID = BaseColumns._ID;
        public static final String STEPS_THUMB = "thumbnailURL";
        public static final String STEPS_VIDEO = "videoURL";
        public static final String STEPS_DESCRIP = "description";
        public static final String STEPS_SHORT_DESCRIP = "shortDescription";
        public static final String STEPS_ASSOCIATED_RECIPE = ASSOCIATED_RECIPE ;
    }
}
