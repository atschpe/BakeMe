package com.example.android.bakeme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.data.adapter.IngredientAdapter;
import com.example.android.bakeme.data.adapter.StepAdapter;
import com.example.android.bakeme.utils.RecipeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * {@link OverviewFragment} is a {@link Fragment} offering the needed ingredients and steps for the
 * selected recipe.
 */
public class OverviewFragment extends Fragment {

    LoadManagerRestarter loadManagerRestarter;

    // Container Activity must implement this interface to track restart calls for the loader.
    public interface LoadManagerRestarter {
        void onLoaderRestarted();
    }

    @Override
    public void onAttach(Context ctxt) {
        super.onAttach(ctxt);
        // ensure DetailActivity has implemented the LoadManagerRestarter
        try {
           loadManagerRestarter = (LoadManagerRestarter) ctxt;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctxt.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    // lists for the recipe in question.
    ArrayList<Ingredients> ingredientsList;
    ArrayList<Steps> stepsList;
    boolean isFavourited;
    Recipe selectedRecipe;

    //Adapters for displaying the ingredients and steps of the recipe in question.
    IngredientAdapter ingredientAdapter;
    StepAdapter stepAdapter;

    //views
    @BindView(R.id.ingredient_rv)
    RecyclerView ingredientRv;
    @BindView(R.id.recipe_steps_rv)
    RecyclerView stepRv;

    @BindView(R.id.overview_favourite_cb)
    CheckBox favButtonCb;

    public OverviewFragment() {
        //required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, root);

        Timber.v("ingredients: %s", ingredientsList);
        Timber.v("steps: %s", stepsList);

        //Setup adapters
        if (ingredientsList != null) {
            ingredientAdapter = new IngredientAdapter(getActivity(), ingredientsList);
            ingredientRv.setAdapter(ingredientAdapter);
            ingredientRv.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
            ingredientAdapter.notifyDataSetChanged();
        }

        if (stepsList != null) {
            stepAdapter = new StepAdapter((DetailActivity) getActivity());
            stepAdapter.setData(getContext(), stepsList);
            stepRv.setAdapter(stepAdapter);
            stepRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            stepAdapter.notifyDataSetChanged();
        }

        //setup favourite button
        if (selectedRecipe.isFavourited()) {
           favButtonCb.setChecked(true);
           Timber.v("boolean – button setup: favourite = true");
        } else {
            favButtonCb.setChecked(false);
            Timber.v("boolean – button setup: favourite = false");
        }

        favButtonCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedRecipe.setFavourited(true);
                    isFavourited = true;
                    ingredientAdapter.setOfferCheckBoxes(true);
                } else {
                    selectedRecipe.setFavourited(false);
                    isFavourited = false;
                    ingredientAdapter.setOfferCheckBoxes(false);
                }
                RecipeUtils.updateFavDb(selectedRecipe, getActivity());
                loadManagerRestarter.onLoaderRestarted();
                ingredientAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(String.valueOf(R.string.STEP_LIST), stepsList);
        outState.putParcelableArrayList(String.valueOf(R.string.INGREDIENT_LIST), ingredientsList);
        super.onSaveInstanceState(outState);
    }

    public void setIngredientsList(ArrayList<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public void setStepsList(ArrayList<Steps> stepsList) {
        this.stepsList = stepsList;
    }

    public void setFavourited(boolean favourited) {
        isFavourited = favourited;
        Timber.v("favourite boolean has been set.");
    }

    public void setSelectedRecipe(Recipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
    }
}
