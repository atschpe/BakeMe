package com.example.android.bakeme;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.testHelpers.RecyclerViewMatcher;
import com.example.android.bakeme.ui.DetailActivity;
import com.example.android.bakeme.utils.RecipeUtils;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

/**
 * This test demos a user clicking on an item in the {@link DetailActivity}.
 */
@RunWith(AndroidJUnit4.class)
public class DetailActivityTester {

    // Convenience helper: https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    private Recipe selectedRecipe;
    private Context ctxt;

    private RecipeIdlingResource idlingResource;

    @Rule
    public final ActivityTestRule<DetailActivity> activityTestRule
            = new ActivityTestRule<DetailActivity>(DetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();
            selectedRecipe = new Recipe(1, "", "Nutella Pie", 8, false);

            Intent testIntent = new Intent(ctxt, DetailActivity.class);
            testIntent.putExtra(RecipeUtils.SELECTED_RECIPE, selectedRecipe);
            return testIntent;
        }
    };

    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    //Simulates clicking on a step to open the method fragment
    @Test
    public void clickRecipeToOpenMethodTest() {

        onView(withId(R.id.recipe_steps_rv)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));

        //https://stackoverflow.com/a/46018815/7601437
        onView(AllOf.allOf(withId(R.id.exo_play),
                withClassName(is(SimpleExoPlayerView.class.getName()))));
    }

    // Simulates favouriting a recipe and checking that
    // (*)it is marked as favourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are revealed
    @Test
    public void favouriteRecipeInDetailTest() {
        onView(withId(R.id.overview_favourite_cb)).check(matches(isNotChecked()));
        onView(withId(R.id.overview_favourite_cb)).perform(click()).check(matches(isChecked()));

        onView(withRecyclerView(R.id.ingredient_rv).atPositionOnView(0,
                R.id.ingredient_cb)).check(matches(isDisplayed()));

        pressBack();
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isChecked()));
    }

    // Simulates unfavouriting a recipe and checking that
    // (*)it is marked as unfavourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are hidden
    @Test
    public void unFavouriteRecipeInDetailTest() {
        onView(withId(R.id.overview_favourite_cb)).check(matches(isChecked()));
        onView(withId(R.id.overview_favourite_cb)).perform(click()).check(matches(isNotChecked()));
        onView(withRecyclerView(R.id.ingredient_rv).atPositionOnView(0,
                R.id.ingredient_cb)).check(matches(not(isDisplayed())));

        pressBack();
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isNotChecked()));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);

    }
}
