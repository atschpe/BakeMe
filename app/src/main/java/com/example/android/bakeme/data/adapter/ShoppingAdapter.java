package com.example.android.bakeme.data.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;

import java.util.ArrayList;

import butterknife.BindView;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {

    private Context ctxt;
    private ArrayList<Recipe.Ingredients> ingredientsList;

    private String prevRecipeName;

    public ShoppingAdapter(Context ctxt, ArrayList<Recipe.Ingredients> ingredientsList) {
        this.ctxt = ctxt;
        this.ingredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(ctxt).inflate(R.layout.widget_ingredient_item, parent, false);
        root.setFocusable(true);
        return new ShoppingViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapter.ShoppingViewHolder holder, int position) {
        Recipe.Ingredients currentIngredient= ingredientsList.get(position);

        String recipeName = currentIngredient.getAssociatedRecipe();
        //only show the recipe name on the first ingredient in the list.
        if (recipeName.equals(prevRecipeName)) {
            holder.widgetDivider.setVisibility(View.GONE);
            holder.widgetRecipe.setVisibility(View.GONE);
        } else {
            holder.widgetDivider.setVisibility(View.VISIBLE);
            holder.widgetRecipe.setVisibility(View.VISIBLE);
            holder.widgetRecipe.setText(recipeName);
            prevRecipeName = recipeName;
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.widget_divider) View widgetDivider;
        @BindView(R.id.widget_recipe_tv)
        TextView widgetRecipe;

        public ShoppingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
