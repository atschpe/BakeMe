package com.example.android.bakeme;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakeme.testHelpers.RecyclerViewMatcher;
import com.example.android.bakeme.ui.DetailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
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

    String description = "Recipe Introduction";

    private RecipeIdlingResource idlingResource;

    @Rule
    public ActivityTestRule<DetailActivity> activityTestRule
            = new ActivityTestRule<>(DetailActivity.class);

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

        onView(withId(R.id.recipe_step_tv)).check(matches(withText(description)));
    }

    // Simulates favouriting a recipe and checking that
    // (*)it is marked as favourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are revealed
    @Test
    public void favouriteRecipeInDetailTest() {
        onView(withId(R.id.overview_favourite_cb)).check(matches(isNotChecked()));
        onView(withId(R.id.overview_favourite_cb)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.ingredient_cb)).check(matches(isDisplayed()));

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
        onView(withId(R.id.ingredient_cb)).check(matches(not(isDisplayed())));

        pressBack();
        onView(withRecyclerView(R.id.recipe_overview_rv).atPositionOnView(0,
                R.id.recipe_favourite_cb)).check(matches(isNotChecked()));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);

    }
}
