package com.example.android.bakeme;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.not;

import com.example.android.bakeme.ui.DetailActivity;
import com.example.android.bakeme.ui.MainActivity;

import org.hamcrest.Matchers;
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

    String recipeTitle = "Nutella Pie";

    private RecipeIdlingResource idlingResource;

    @Rule
    private ActivityTestRule<MainActivity> activityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    //Simulate clicking on recipe card to open DetailActivity
    public void clickRvItemToOpenDetailTest() {

        onData(anything()).inAdapterView(withId(R.id.recipe_overview_rv)).atPosition(0)
                .perform(click());

        onView(withId(android.R.id.title)).check(matches(withText(recipeTitle)));
    }

    // Simulates favouriting a recipe and checking that
    // (*)it is marked as favourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are revealed
    @Test
    public void favouriteRecipeInDetailTest() {
        onView(withId(R.id.recipe_favourite_cb)).check(matches(isNotChecked()));
        onView(withId(R.id.recipe_favourite_cb)).perform(click())
                .check(matches(isChecked()));
        onView(withId(R.id.ingredient_cb)).check(matches(isDisplayed()));

        onData(Matchers.anything()).inAdapterView(withId(R.id.ingredient_cb))
                .check(matches(isChecked()));
    }

    // Simulates unfavouriting a recipe and checking that
    // (*)it is marked as unfavourited both in the Detail~ and the MainActivity
    // (*)the ingredient checkboxes are hidden
    @Test
    public void unFavouriteRecipeInDetailTest() {
        onView(withId(R.id.recipe_favourite_cb)).check(matches(isChecked()));
        onView(withId(R.id.recipe_favourite_cb)).perform(click()).check(matches(isNotChecked()));
        onView(withId(R.id.ingredient_cb)).check(matches(not(isDisplayed())));

        onData(Matchers.anything()).inAdapterView(withId(R.id.overview_favourite_cb))
                .check(matches(isNotChecked()));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);

    }
}
