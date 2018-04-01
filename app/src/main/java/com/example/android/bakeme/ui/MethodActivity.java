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

    private Recipe selectedRecipe;
    private Steps step;
    private ArrayList<Steps> stepsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);

        Intent recipeReceived = getIntent();
        Bundle recipeBundle = recipeReceived.getBundleExtra(String.valueOf(
                RecipeUtils.RECIPE_BUNDLE));

        selectedRecipe = recipeBundle.getParcelable(String.valueOf(RecipeUtils.SELECTED_RECIPE));
        step = recipeBundle.getParcelable(String.valueOf(RecipeUtils.SELECTED_STEP));
        stepsList = recipeBundle.getParcelableArrayList(String.valueOf(RecipeUtils.STEP_LIST));

        methodFrag = new MethodFragment();
        fragMan = getSupportFragmentManager();

        methodFrag.setStep(step);
        methodFrag.setRecipe(selectedRecipe);
        methodFrag.setStepsList(stepsList);

        fragMan.beginTransaction().add(R.id.method_container, methodFrag).addToBackStack(null)
                .commit();
    }
}
