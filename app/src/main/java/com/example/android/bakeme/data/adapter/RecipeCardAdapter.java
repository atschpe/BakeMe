package com.example.android.bakeme.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.utils.RecipeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        void onFavClick(Recipe recipe, int recipePostion);
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
        root.setFocusable(true);
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

        long recipeId = currentRecipe.getId();
        List<Steps> currentStep = currentRecipe.getSteps();

        //currently there are no images available in the api, but assuming it would be updated at
        //some point this code will display the image or a thumbnail.
        String recipeImage = null;
        //get image if available
        if (!currentRecipe.getImage().isEmpty()) {
            recipeImage = currentRecipe.getImage();

            //if there is no image, get the last thumbnail in the list
        }

        Picasso.with(ctxt).load(recipeImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.cardImageIv);

        String servingText = "Serves: " + currentRecipe.getServings();
        holder.cardServingTv.setText(servingText);

        holder.cardNameTv.setText(currentRecipe.getName());

        boolean isFavourited = currentRecipe.getFavourited() == R.integer.is_favourited;
        RecipeUtils.setfavButton(isFavourited, holder.favouriteIb, ctxt);

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
        @BindView(R.id.favourite_ib)
        ImageButton favouriteIb;

        /**
         * super constructor
         *
         * @param itemView is the holder in question.
         */
        RecipeCardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            favouriteIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe currentRecipe = recipeList.get(getAdapterPosition());
                    recipeClicker.onFavClick(currentRecipe, getAdapterPosition());
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