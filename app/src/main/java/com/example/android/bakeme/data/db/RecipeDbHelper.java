package com.example.android.bakeme.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.db.RecipeContract.RecipeEntry;

import static com.example.android.bakeme.data.db.RecipeContract.*;

public class RecipeDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 1;

    public RecipeDbHelper(Context ctxt) {
        super(ctxt, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE = "CREATE TABLE ";
    private static final String START = " (";
    private static final String CLOSE = ");";
    private static final String COMMA = " ,";
    private static final String INT_AUTO = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String INT_REQ = " INTEGER NOT NULL";
    private static final String INT = " INTEGER";
    private static final String BOO = " INTEGER DEFAULT 0";
    private static final String TEXT_REQ = " TEXT NOT NULL";
    private static final String TEXT = " TEXT";
    private static final String REAL_REQ = " REAL NOT NULL";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RECIPE_TABLE = CREATE + RecipeEntry.TABLE_RECIPE + START
                + RecipeEntry.RECIPE_ID + INT_REQ + COMMA
                + RecipeEntry.RECIPE_IMAGE + TEXT + COMMA
                + RecipeEntry.RECIPE_NAME + TEXT_REQ + COMMA
                + RecipeEntry.RECIPE_FAVOURITED + BOO + COMMA
                + RecipeEntry.RECIPE_SERVINGS + INT_REQ + CLOSE;
        db.execSQL(SQL_CREATE_RECIPE_TABLE);

        final String SQL_CREATE_INGREDIENTS_TABLE = CREATE + IngredientsEntry.TABLE_INGREDIENTS + START
                + IngredientsEntry.INGREDIENTS_GLOBAL_ID + INT_AUTO + COMMA
                + IngredientsEntry.INGREDIENTS_ID + INT_REQ + COMMA
                + IngredientsEntry.INGREDIENTS_INGREDIENT + TEXT_REQ + COMMA
                + IngredientsEntry.INGREDIENTS_MEASURE + TEXT_REQ + COMMA
                + IngredientsEntry.INGREDIENTS_QUANTITY + REAL_REQ + COMMA
                + IngredientsEntry.INGREDIENTS_CHECKED + INT + COMMA
                + IngredientsEntry.INGREDIENTS_ASSOCIATED_RECIPE + TEXT + CLOSE;
        db.execSQL(SQL_CREATE_INGREDIENTS_TABLE);

        final String SQL_CREATE_STEPS_TABLE = CREATE + StepsEntry.TABLE_STEPS + START
                + StepsEntry.STEPS_GLOBAL_ID+ INT_AUTO + COMMA
                + StepsEntry.STEPS_ID + INT_REQ + COMMA
                + StepsEntry.STEPS_SHORT_DESCRIP + TEXT_REQ + COMMA
                + StepsEntry.STEPS_DESCRIP + TEXT_REQ + COMMA
                + StepsEntry.STEPS_THUMB + TEXT + COMMA
                + StepsEntry.STEPS_VIDEO + TEXT + COMMA
                + StepsEntry.STEPS_ASSOCIATED_RECIPE + CLOSE;
        db.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    //TODO: make upgrade more dynamic (see comment on last project
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + RecipeEntry.TABLE_RECIPE);
        db.execSQL(DROP_TABLE + IngredientsEntry.TABLE_INGREDIENTS);
        db.execSQL(DROP_TABLE + StepsEntry.TABLE_STEPS);
        onCreate(db);
    }
}
