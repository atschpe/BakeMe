package com.example.android.bakeme.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Ingredients;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.data.adapter.IngredientAdapter;
import com.example.android.bakeme.data.adapter.StepAdapter;
import com.example.android.bakeme.utils.RecipeUtils;
import com.example.android.bakeme.widget.IngredientsService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * {@link OverviewFragment} is a {@link Fragment} offering the needed ingredients and steps for the
 * selected recipe.
 */
public class OverviewFragment extends Fragment {

    private OnLoadManagerRestart loadManagerRestart;

    // Container Activity must implement this interface to track restart calls for the loader.
    public interface OnLoadManagerRestart {
        void onLoaderRestarted();
    }

    @Override
    public void onAttach(Context ctxt) {
        super.onAttach(ctxt);
        // ensure DetailActivity has implemented the LoadManagerRestarter
        try {
           loadManagerRestart = (OnLoadManagerRestart) ctxt;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctxt.toString()
                    + " must implement LoadManagerRestart");
        }
    }

    // lists for the recipe in question.
    private ArrayList<Ingredients> ingredientsList;
    private ArrayList<Steps> stepsList;

    private Recipe selectedRecipe;

    //Adapters for displaying the ingredients and steps of the recipe in question.
    private IngredientAdapter ingredientAdapter;
    private StepAdapter stepAdapter;

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
        final View root = inflater.inflate(R.layout.fragment_overview, container,
                false);
        ButterKnife.bind(this, root);

        //Setup adapters
        if (ingredientsList != null) {
            ingredientAdapter = new IngredientAdapter((DetailActivity) getActivity());
            ingredientAdapter.setData(getContext(), ingredientsList);
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
           ingredientAdapter.setOfferCheckBoxes(true);
           Timber.v("boolean – button setup: favourite = true");
        } else {
            favButtonCb.setChecked(false);
            Timber.v("boolean – button setup: favourite = false");
        }
        favButtonCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox)v).isChecked();
                Timber.v("Recipe id: "+ selectedRecipe.getId());
                if (checked) {
                    selectedRecipe.setFavourited(true);
                    ingredientAdapter.setOfferCheckBoxes(true);
                    Snackbar infoSnackbar = Snackbar.make(root, R.string.favourite_snack_message,
                            Snackbar.LENGTH_LONG);
                    infoSnackbar.setAction("Learn more",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog dialogBuilder
                                            = new AlertDialog.Builder(getActivity()).create();
                                    dialogBuilder.setMessage(getString(
                                            R.string.favourite_info_dialog));
                                    dialogBuilder.show();
                                }
                            });
                    infoSnackbar.show();
                } else {
                    selectedRecipe.setFavourited(false);
                    ingredientAdapter.setOfferCheckBoxes(false);
                }
                RecipeUtils.updateFavDb(selectedRecipe, getActivity());
                loadManagerRestart.onLoaderRestarted();
                ingredientAdapter.notifyDataSetChanged();
                RecipeUtils.setFavIsUpdated(true);
                IngredientsService.startHandleActionUpdateWidget(getActivity());
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(String.valueOf(RecipeUtils.STEP_LIST), stepsList);
        outState.putParcelableArrayList(String.valueOf(RecipeUtils.INGREDIENT_LIST),
                ingredientsList);
        super.onSaveInstanceState(outState);
    }

    public void setIngredientsList(ArrayList<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public void setStepsList(ArrayList<Steps> stepsList) {
        this.stepsList = stepsList;
    }

    public void setFavourited(boolean favourited) {
        Timber.v("favourite boolean has been set.");
    }

    public void setSelectedRecipe(Recipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
    }
}
