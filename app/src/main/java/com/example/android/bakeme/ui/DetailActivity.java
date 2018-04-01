package com.example.android.bakeme.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakeme.R;
import com.example.android.bakeme.RecipeIdlingResource;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.data.adapter.IngredientAdapter;
import com.example.android.bakeme.data.adapter.StepAdapter;
import com.example.android.bakeme.data.db.RecipeContract.IngredientsEntry;
import com.example.android.bakeme.data.db.RecipeContract.RecipeEntry;
import com.example.android.bakeme.data.db.RecipeContract.StepsEntry;
import com.example.android.bakeme.utils.RecipeUtils;
import com.example.android.bakeme.widget.IngredientsService;

import java.util.ArrayList;

import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements StepAdapter.StepClickHandler,
        IngredientAdapter.IngredientClickHandler, LoaderManager.LoaderCallbacks<Cursor>,
        OverviewFragment.OnLoadManagerRestart {

    //booleans to track layout & favouriting action
    private static boolean twoPane;
    private static ArrayList<Ingredients> ingredientsList;
    private static ArrayList<Steps> stepsList;
    //variables to keep track of loaders finishing
    private final int amountOfLoaders = 3;
    private Recipe selectedRecipe;
    private boolean isFavourited;
    private boolean loaderIsRestarted = false;
    private OverviewFragment overviewFrag;
    private MethodFragment methodFrag;
    private FragmentManager fragMan;
    private int completedLoaders = 0;

    // Idling resource for testing purposes only
    @Nullable
    private RecipeIdlingResource idlingResource;

    /**
     * Only called from test, creates and returns a new {@link RecipeIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public RecipeIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new RecipeIdlingResource();
        }
        return idlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        twoPane = findViewById(R.id.detail_fragment_container2) != null;

        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);

        //instantiate lists ready to retrieve the provided information for each.
        ingredientsList = new ArrayList<>();
        stepsList = new ArrayList<>();

        fragMan = getSupportFragmentManager();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(String.valueOf(RecipeUtils.SELECTED_RECIPE))) {
                selectedRecipe = savedInstanceState.getParcelable(String
                        .valueOf(RecipeUtils.SELECTED_RECIPE));
            }
            if (savedInstanceState.containsKey(String.valueOf(RecipeUtils.INGREDIENT_LIST))) {
                ingredientsList = savedInstanceState.getParcelableArrayList(String
                        .valueOf(RecipeUtils.INGREDIENT_LIST));
            }
            if (savedInstanceState.containsKey(String.valueOf(RecipeUtils.STEP_LIST))) {
                stepsList = savedInstanceState.getParcelableArrayList(String
                        .valueOf(RecipeUtils.STEP_LIST));
            }

        } else {
            Intent recipeIntent = getIntent();
            Timber.v("recipe Intent: %s", recipeIntent);
            if (recipeIntent != null
                    && recipeIntent.hasExtra(String.valueOf(RecipeUtils.SELECTED_RECIPE))) {
                selectedRecipe
                        = recipeIntent.getParcelableExtra(String.valueOf(
                                RecipeUtils.SELECTED_RECIPE));
            }

            overviewFrag = new OverviewFragment();
            methodFrag = new MethodFragment();
        }

        //set up the loaderManagers to retrieve the needed data
        getSupportLoaderManager().initLoader(RecipeUtils.RECIPE_DETAIL_LOADER, null,
                this);
        getSupportLoaderManager().initLoader(RecipeUtils.INGREDIENTS_DETAIL_LOADER, null,
                this);
        getSupportLoaderManager().initLoader(RecipeUtils.STEPS_DETAIL_LOADER, null,
                this);

        getSupportActionBar().setTitle(selectedRecipe.getName());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnToMain = new Intent(this, MainActivity.class);
        int CODE;
        if (RecipeUtils.isFavIsUpdated()) {
            CODE = RecipeUtils.FAV_UPDATED_FLAG;
        } else {
            CODE = RecipeUtils.FAV_UNCHANGED_FLAG;
        }
        RecipeUtils.setFavIsUpdated(false);
        startActivityForResult(returnToMain, CODE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(String.valueOf(RecipeUtils.SELECTED_RECIPE), selectedRecipe);
        outState.putParcelableArrayList(String.valueOf(RecipeUtils.INGREDIENT_LIST),
                ingredientsList);
        outState.putParcelableArrayList(String.valueOf(RecipeUtils.STEP_LIST), stepsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(Steps step) {

        if (twoPane) {
            methodFrag.setStep(step);
            methodFrag.setRecipe(selectedRecipe);
            methodFrag.setStepsList(stepsList);

            fragMan.beginTransaction().replace(R.id.detail_fragment_container2, methodFrag)
                    .addToBackStack(null).commit();
        } else {
            MethodActivity.setStep(step);
            MethodActivity.setSelectedRecipe(selectedRecipe);
            MethodActivity.setStepsList(stepsList);
            Intent openMethod = new Intent(this, MethodActivity.class);
            startActivity(openMethod);
        }
    }

    // see: https://stackoverflow.com/a/11421298/7601437
    private void loaderHasFinished() {
        completedLoaders++;
        if (completedLoaders == amountOfLoaders) {
            setupFragments();
            completedLoaders = 0;
        }
    }

    private void setupFragments() {
        overviewFrag = new OverviewFragment();
        overviewFrag.setIngredientsList(ingredientsList);
        overviewFrag.setStepsList(stepsList);
        overviewFrag.setFavourited(isFavourited);
        Timber.v("set boolean for favourited");
        overviewFrag.setSelectedRecipe(selectedRecipe);

        fragMan.beginTransaction().add(R.id.detail_fragment_container1, overviewFrag)
                .addToBackStack(null).commit();

        if (twoPane) {
            methodFrag = new MethodFragment();
            methodFrag.setStep(stepsList.get(0));
            methodFrag.setRecipe(selectedRecipe);
            methodFrag.setStepsList(stepsList);

            fragMan.beginTransaction().add(R.id.detail_fragment_container2, methodFrag).commit();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Uri uri = null;
        String[] projection = new String[0];
        String selection = null;
        String[] selectionArgs = new String[0];
        String selectedRecipeName = selectedRecipe.getName();
        RecipeUtils.setCurrentRecipeName(selectedRecipeName);
        switch (id) {
            case RecipeUtils.RECIPE_DETAIL_LOADER:
                uri = RecipeEntry.CONTENT_URI_RECIPE;
                projection = new String[]{RecipeEntry.RECIPE_FAVOURITED};
                selection = RecipeEntry.RECIPE_NAME + "=?";
                selectionArgs = new String[]{selectedRecipeName};
                break;
            case RecipeUtils.INGREDIENTS_DETAIL_LOADER:
                uri = IngredientsEntry.CONTENT_URI_INGREDIENTS;
                selection = IngredientsEntry.INGREDIENTS_ASSOCIATED_RECIPE + "=?";
                selectionArgs = new String[]{String.valueOf(selectedRecipeName)};
                break;
            case RecipeUtils.STEPS_DETAIL_LOADER:
                uri = StepsEntry.CONTENT_URI_STEPS;
                selection = StepsEntry.STEPS_ASSOCIATED_RECIPE + "=?";
                selectionArgs = new String[]{String.valueOf(selectedRecipeName)};
                break;
        }
        return new CursorLoader(this, uri, projection, selection, selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case RecipeUtils.RECIPE_DETAIL_LOADER:
                if (data != null && data.getCount() > 0) {
                    data.moveToFirst();
                    int favValue = data.getInt(data.getColumnIndex(RecipeEntry.RECIPE_FAVOURITED));
                    isFavourited = favValue == RecipeEntry.FAVOURITED_TRUE;
                    selectedRecipe.setFavourited(isFavourited);
                }
                break;
            case RecipeUtils.INGREDIENTS_DETAIL_LOADER:
                if (data != null && data.getCount() > 0) {
                    RecipeUtils.getIngredientList(data, ingredientsList);
                }
                break;
            case RecipeUtils.STEPS_DETAIL_LOADER:
                if (data != null && data.getCount() > 0) {
                    RecipeUtils.getStepsList(data, stepsList);
                }
                break;
        }
        //not closing cursor, as Loader takes care of this:
        // https://developer.android.com/guide/components/loaders.html#onLoadFinished

        if (loaderIsRestarted) {
            overviewFrag.setSelectedRecipe(selectedRecipe);
            loaderIsRestarted = false;
        } else {
            //keep track of each loader finishing so the fragments start with all data on hand.
            loaderHasFinished();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //not needed
    }

    @Override
    public void onLoaderRestarted() {
        loaderIsRestarted = true;
        getSupportLoaderManager().restartLoader(RecipeUtils.INGREDIENTS_DETAIL_LOADER, null,
                this);
    }

    @Override
    public void onIngredientClick(Ingredients selectedIngredient,
                                  boolean isChecked) {
        if (isChecked) {
            selectedIngredient.setChecked(true);
        } else {
            selectedIngredient.setChecked(false);
        }
        //update the db and then restart the loader.
        RecipeUtils.updateCheckedDb(selectedIngredient, this);
        getSupportLoaderManager().restartLoader(RecipeUtils.INGREDIENTS_DETAIL_LOADER, null,
                this);
        IngredientsService.startHandleActionUpdateWidget(this);
    }
}