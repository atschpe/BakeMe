package com.example.android.bakeme.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe.Steps;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link StepAdapter} is a {@link RecyclerView.Adapter} to display the ingredients for the
 * recipe in question.
 */
public class StepAdapter
        extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    final private StepAdapter.StepClickHandler stepClicker;

    //setData frees up the constructor to be used in DetailActivity.
    // Based on: http://www.i-programmer.info/professional-programmer/accreditation/10908-insiders-guide-to-udacity-android-developer-nanodegree-part-3-the-making-of-baking-app.html?start=1
    public void setData(Context ctxt, ArrayList<Steps> stepsList) {
        this.ctxt = ctxt;
        this.stepsList = stepsList;
    }

    public interface StepClickHandler {
        void onClick(Steps step);
    }

    private Context ctxt;
    private ArrayList<Steps> stepsList;

    public StepAdapter(StepClickHandler stepClicker) {
        this.stepClicker = stepClicker;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View root = LayoutInflater.from(ctxt).inflate(R.layout.recipe_step_shortdescrip, parent,
                false);
        root.setFocusable(true);
        return new StepViewHolder(root);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        Steps currentItem = this.stepsList.get(position);

        if (currentItem.getId() == 0) { // don't show step "0" number
            holder.stepNoTv.setVisibility(View.INVISIBLE);
        } else {
            holder.stepNoTv.setText(String.valueOf(currentItem.getId()));
        }

        holder.recipeTextTv.setText(currentItem.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (stepsList == null) return 0;
        else return stepsList.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_text_tv)
        TextView recipeTextTv;
        @BindView(R.id.stepNo_tv)
        TextView stepNoTv;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Steps currentStep = stepsList.get(getAdapterPosition());
            stepClicker.onClick(currentStep);
        }
    }
}