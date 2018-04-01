package com.example.android.bakeme.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.utils.RecipeUtils;

import java.util.ArrayList;

public class MethodActivity extends AppCompatActivity {

    private MethodFragment methodFrag;
    private FragmentManager fragMan;
    private static Recipe selectedRecipe;

    public static void setSelectedRecipe(Recipe selectedRecipe) {
        MethodActivity.selectedRecipe = selectedRecipe;
    }

    public static void setStep(Steps step) {
        MethodActivity.step = step;
    }

    public static void setStepsList(ArrayList<Steps> stepsList) {
        MethodActivity.stepsList = stepsList;
    }

    private static Steps step;
    private static ArrayList<Steps> stepsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
//
//        Intent recipeReceived = getIntent();
//        Bundle recipeBundle = recipeReceived.getBundleExtra(RecipeUtils.RECIPE_BUNDLE);
//
//        selectedRecipe = recipeBundle.getParcelable(RecipeUtils.SELECTED_RECIPE);
//        step = recipeBundle.getParcelable(RecipeUtils.SELECTED_STEP);
//        stepsList = getIntent().getParcelableArrayListExtra(RecipeUtils.STEP_LIST);

        if (savedInstanceState == null) {

        methodFrag = new MethodFragment();
        fragMan = getSupportFragmentManager();

            methodFrag.setStep(step);
            methodFrag.setRecipe(selectedRecipe);
            methodFrag.setStepsList(stepsList);

            fragMan.beginTransaction().add(R.id.method_container, methodFrag).commit();
        }
    }
}
