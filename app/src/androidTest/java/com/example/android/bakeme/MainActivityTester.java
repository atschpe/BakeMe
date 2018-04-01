package com.example.android.bakeme;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import com.example.android.bakeme.testHelpers.MyViewAction;
import com.example.android.bakeme.testHelpers.RecyclerViewMatcher;
import com.example.android.bakeme.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test demos a user clicking on an item in the {@link MainActivity}.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTester {

    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    private RecipeIdlingResource idlingResource;

    // Convenience helper: https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
    private final String ingredient = "2 cup Graham Cracker crumbs";

    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    //Simulate clicking on recipe card to open DetailActivity √
    public void clickRvItemToOpenDetailTest() {

        onView(withId(R.id.recipe_overview_rv)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));

        onView(withRecyclerView(R.id.ingredient_rv)
                .atPositionOnView(0, R.id.ingredient_tv))
                .check(matches(withText(ingredient)));
    }

    // Simulates favouriting a recipe and checking that
    // (*)it is marked as favourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are revealed
    @Test
    public void favouriteRecipeInDetailTest() {
        //in MainActivity: ensure fav button is not checked before checking it and ensure it
        // registered being checked
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isNotChecked()));
        onView(withId(R.id.recipe_overview_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0,
                        MyViewAction.clickChildViewWithId(R.id.recipe_favourite_cb)));
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isChecked()));

        //in DetailActivity: ensure the fav button is active and ingredient checkboxes are visible
        onView(withId(R.id.recipe_overview_rv)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));
        onView(withId(R.id.overview_favourite_cb)).check(matches(isChecked()));
        onView(withRecyclerView(R.id.ingredient_rv).atPositionOnView(0,
                R.id.ingredient_cb)).check(matches(isDisplayed()));

    }

    // Simulates unfavouriting a recipe and checking that
    // (*)it is marked as unfavourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are hidden
    @Test
    public void unFavouriteRecipeInDetailTest() {
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isChecked()));
        onView(withId(R.id.recipe_overview_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0,
                        MyViewAction.clickChildViewWithId(R.id.recipe_favourite_cb)));
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isNotChecked()));

        onView(withId(R.id.recipe_overview_rv)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));
        onView(withId(R.id.overview_favourite_cb)).check(matches(isNotChecked()));
        onView(withRecyclerView(R.id.ingredient_rv).atPositionOnView(0,
                R.id.ingredient_cb)).check(matches(not(isDisplayed())));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);

    }
}
