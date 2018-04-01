package com.example.android.bakeme.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * {@link RecipeCardAdapter} is a {@link RecyclerView.Adapter} to populate the RecipeCards.
 */
public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeCardHolder> {

    private final Context ctxt;
    private final ArrayList<Recipe> recipeList;

    /**
     * Constructor
     *
     * @param ctxt          is the context wherein the adapter operates
     * @param recipeList    is a list of parts pertaining to the recipe in question
     * @param recipeClicker is the click handler for the recipe chosen by the user.
     */
    public RecipeCardAdapter(Context ctxt, ArrayList<Recipe> recipeList,
                             RecipeClickHandler recipeClicker) {
        this.ctxt = ctxt;
        this.recipeList = recipeList;
        this.recipeClicker = recipeClicker;
    }

    final private RecipeClickHandler recipeClicker;

    public interface RecipeClickHandler {
        void onClick(Recipe recipe);

        void onFavClick(Recipe recipe, boolean isChecked);
    }

    /**
     * Create a root view to hold the information provided for the Recipe card.
     *
     * @param parent   of the view.
     * @param viewType in case various root views are used (not needed)
     * @return a new view ready to populate.
     */
    @Override
    public RecipeCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(ctxt).inflate(R.layout.recipe_card, parent, false);
        //root.setFocusable(true);
        return new RecipeCardHolder(root);
    }

    /**
     * Populate the view with the provided information.
     *
     * @param holder   is the view to be populated.
     * @param position within the adapter.
     */
    @Override
    public void onBindViewHolder(final RecipeCardHolder holder, int position) {
        final Recipe currentRecipe = this.recipeList.get(position);
        Timber.v("recipeList:" + recipeList.size());

        //currently there are no images available in the api, but assuming it would be updated at
        //some point this code will display the image or a thumbnail.
        String recipeImage = null;
        //get image if available
        if (!currentRecipe.getImage().isEmpty()) {
            recipeImage = currentRecipe.getImage();

            //todo: if there is no image, get the last thumbnail in the list
        }

        Picasso.with(ctxt).load(recipeImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.cardImageIv);

        String servingText = String.valueOf(currentRecipe.getServings());
        holder.cardServingTv.setText(servingText);

        holder.cardNameTv.setText(currentRecipe.getName());

        if (currentRecipe.isFavourited()) {
            holder.favouriteCb.setChecked(true);
        } else {
            holder.favouriteCb.setChecked(false);
        }
    }

    /**
     * amount of items to be displayed in the adapter
     *
     * @return null if the list is empty, or the list size.
     */
    @Override
    public int getItemCount() {
        if (recipeList == null) {
            return 0;
        } else {
            return recipeList.size();
        }
    }

    /**
     * {@link RecipeCardHolder} is a {@link RecyclerView.ViewHolder} to support creation of views
     * to populate. It implements an {@link View.OnClickListener} to communicate the position for
     * further use.
     */
    public class RecipeCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_image_iv)
        ImageView cardImageIv;
        @BindView(R.id.card_name_tv)
        TextView cardNameTv;
        @BindView(R.id.card_serving_tv)
        TextView cardServingTv;
        @BindView(R.id.recipe_favourite_cb)
        CheckBox favouriteCb;

        /**
         * super constructor
         *
         * @param itemView is the holder in question.
         */
        RecipeCardHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            favouriteCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe currentRecipe = recipeList.get(getAdapterPosition());
                    boolean checked = ((CheckBox)v).isChecked();
                    recipeClicker.onFavClick(currentRecipe, checked);
                }
            });
        }

        /**
         * provide the selected Recipe when clicked
         *
         * @param v is the view in question.
         */
        @Override
        public void onClick(View v) {
            Recipe currentRecipe = recipeList.get(getAdapterPosition());
            recipeClicker.onClick(currentRecipe);
        }
    }
}