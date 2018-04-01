package com.example.android.bakeme.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe.Ingredients;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link IngredientAdapter} is a {@link RecyclerView.Adapter} to display the ingredients for the
 * recipe in question.
 */
public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Context ctxt;
    private ArrayList<Ingredients> ingredientsList;

    private boolean offerCheckBoxes;

    public boolean isOfferCheckBoxes() {
        return offerCheckBoxes;
    }

    public void setOfferCheckBoxes(boolean offerCheckBoxes) {
        this.offerCheckBoxes = offerCheckBoxes;
    }

    public IngredientAdapter(IngredientClickHandler ingredientClicker) {
        this.ingredientClicker = ingredientClicker;
    }

    final private IngredientClickHandler ingredientClicker;

    public void setData(Context ctxt, ArrayList<Ingredients> ingredientsList) {
        this.ctxt = ctxt;
        this.ingredientsList = ingredientsList;
    }

    public interface IngredientClickHandler {
        void onIngredientClick(Ingredients ingredients, int ingredientPostion, boolean isChecked);

    }

    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View root = LayoutInflater.from(ctxt).inflate(R.layout.ingredient_item, parent,
                false);
        root.setFocusable(true);
        return new IngredientViewHolder(root);
    }

    @Override
    public void onBindViewHolder(IngredientAdapter.IngredientViewHolder holder, int position) {
        final Ingredients currentItem = ingredientsList.get(position);

        holder.ingredientTv.setText(currentItem.toString());

        if (offerCheckBoxes) {
            holder.ingredientCb.setVisibility(View.VISIBLE);
            if(currentItem.isChecked()) {
                holder.ingredientCb.setChecked(true);
            } else {
                holder.ingredientCb.setChecked(false);
            }
        } else {
            holder.ingredientCb.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (ingredientsList == null) return 0;
        else return ingredientsList.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredient_tv)
        TextView ingredientTv;
        @BindView(R.id.ingredient_cb)
        CheckBox ingredientCb;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ingredientCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ingredients currentIngredient = ingredientsList.get(getAdapterPosition());
                    boolean checked = ((CheckBox) v).isChecked();
                    ingredientClicker.onIngredientClick(currentIngredient, getAdapterPosition(),
                            checked);
                }
            });
        }
    }

}

