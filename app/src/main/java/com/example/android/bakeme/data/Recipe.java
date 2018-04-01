package com.example.android.bakeme.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.bakeme.data.db.RecipeContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Retrofit;

import static com.example.android.bakeme.data.Recipe.Ingredients.TABLE_INGREDIENTS;
import static com.example.android.bakeme.data.Recipe.Steps.TABLE_STEPS;

/**
 * {@link Recipe} is an object holding the various infos provided by the API concerning all
 * aspects of the recipe in question. It is setup to using http://www.jsonschema2pojo.org/
 * to enable {@link Retrofit} to use it and implements {@link Parcelable} to enable
 * data persistence.
 */
@Entity(tableName = Recipe.TABLE_RECIPE)
public class Recipe implements Parcelable {

    static final String ASSOCIATED_RECIPE = "associatedRecipe";

    //db table as well as api keys
    public static final String TABLE_RECIPE = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_IMAGE = "image";
    public static final String RECIPE_SERVINGS = "servings";
    public static final String RECIPE_STEPS = "steps";
    public static final String RECIPE_INGREDIENTS = "ingredients";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_FAVOURITED = " favourited";

    @ColumnInfo(name = RECIPE_IMAGE)
    @Expose
    @SerializedName(RECIPE_IMAGE)
    private String image;

    @ColumnInfo(name = RECIPE_SERVINGS)
    @Expose
    @SerializedName(RECIPE_SERVINGS)
    private int servings;

    @Ignore
    @Expose
    @SerializedName( RECIPE_STEPS)
    private List<Steps> steps;

    @Ignore
    @Expose
    @SerializedName(RECIPE_INGREDIENTS)
    private List<Ingredients> ingredients;

    @ColumnInfo(name = RECIPE_NAME)
    @Expose
    @SerializedName(RECIPE_NAME)
    private String name;

    @PrimaryKey
    @ColumnInfo(index = true, name = RECIPE_ID)
    @Expose
    @SerializedName(RECIPE_ID)
    private long id;

    //not part of the api, but used to track favourited recipes for the widget
    @ColumnInfo(name = RECIPE_FAVOURITED)
    private boolean favourited =false;

    @Ignore
    public Recipe(int id, String image, String name, int servings, boolean favourited) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.servings = servings;
        this.favourited = favourited;
    }

    public Recipe() {
    }

    //Create a new recipe from the offered ContentValues.
    public static Recipe fromContentValues(ContentValues values) {
        final Recipe recipe = new Recipe();

        if (values.containsKey(RECIPE_ID)) {
            recipe.id = values.getAsLong(RECIPE_ID);
        }
        if (values.containsKey(RECIPE_IMAGE)) {
            recipe.image = values.getAsString(RECIPE_IMAGE);
        }
        if (values.containsKey(RECIPE_SERVINGS)) {
            recipe.servings = values.getAsInteger(RECIPE_SERVINGS);
        }
        if (values.containsKey(RECIPE_NAME)) {
            recipe.name = values.getAsString(RECIPE_NAME);
        }
        if (values.containsKey(RECIPE_FAVOURITED)) {
            recipe.favourited = values.getAsBoolean(RECIPE_FAVOURITED);
        }
        return recipe;
    }

    protected Recipe(Parcel in) {
        image = in.readString();
        servings = in.readInt();
        name = in.readString();
        id = in.readInt();
        favourited = in.readByte() != 0;

//        //inner classes
//        ingredients = new ArrayList<>();
//        in.readList(ingredients, Ingredients.class.getClassLoader());
//
//        steps = new ArrayList<>();
//        in.readList(steps, Steps.class.getClassLoader());
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeInt(servings);
        dest.writeString(name);
        dest.writeLong(id);
        dest.writeByte((byte) (favourited ? 1 : 0));

        //inner classes
        dest.writeList(ingredients);
        dest.writeList(steps);
    }

    //see https://stackoverflow.com/a/44889919/7601437 for implementation (also for ingredients)
    @Entity(tableName = TABLE_STEPS)
    public static class Steps implements Parcelable {

        public static final String TABLE_STEPS = "steps";
        public static final String STEPS_ID = "id";
        public static final String STEPS_GLOBAL_ID = "globalId";
        public static final String STEPS_THUMB = "thumbnailURL";
        public static final String STEPS_VIDEO = "videoURL";
        public static final String STEPS_DESCRIP = "description";
        public static final String STEPS_SHORT_DESCRIP = "shortDescription";
        public static final String STEPS_ASSOCIATED_RECIPE = ASSOCIATED_RECIPE ;

        @ColumnInfo(name = STEPS_THUMB)
        @Expose
        @SerializedName(STEPS_THUMB)
        private String thumbnail;

        @ColumnInfo(name = STEPS_VIDEO)
        @Expose
        @SerializedName(STEPS_VIDEO)
        private String video;

        @ColumnInfo(name = STEPS_DESCRIP)
        @Expose
        @SerializedName(STEPS_DESCRIP)
        private String description;

        @ColumnInfo(name = STEPS_SHORT_DESCRIP)
        @Expose
        @SerializedName(STEPS_SHORT_DESCRIP)
        private String shortDescription;

        @ColumnInfo(name = STEPS_ASSOCIATED_RECIPE)
        private String associatedRecipe;

        @ColumnInfo(index = true, name = STEPS_ID)
        @Expose
        @SerializedName(STEPS_ID)
        private long id;

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(index = true, name = STEPS_GLOBAL_ID)
        private long globalId;


        public Steps() {
        }

        @Ignore
        public Steps(long id, String shortDescription, String description, String video,
                     String thumbnail) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.video = video;
            this.thumbnail = thumbnail;
        }

        public static Steps fromContentValues(ContentValues values) {
            Steps steps = new Steps();
            if (values.containsKey(STEPS_ID)) {
                steps.id = values.getAsInteger(STEPS_ID);
            }
            if (values.containsKey(STEPS_THUMB)) {
                steps.thumbnail = values.getAsString(STEPS_THUMB);
            }
            if (values.containsKey(STEPS_VIDEO)) {
                steps.video = values.getAsString(STEPS_VIDEO);
            }
            if (values.containsKey(STEPS_DESCRIP)) {
                steps.description = values.getAsString(STEPS_DESCRIP);
            }
            if (values.containsKey(STEPS_SHORT_DESCRIP)) {
                steps.shortDescription = values.getAsString(STEPS_SHORT_DESCRIP);
            }
            if (values.containsKey(STEPS_ASSOCIATED_RECIPE)) {
                steps.associatedRecipe = values.getAsString(STEPS_ASSOCIATED_RECIPE);
            }
            if (values.containsKey(STEPS_GLOBAL_ID)) {
                steps.globalId = values.getAsLong(STEPS_GLOBAL_ID);
            }
            return steps;
        }

        protected Steps(Parcel in) {
            thumbnail = in.readString();
            video = in.readString();
            description = in.readString();
            shortDescription = in.readString();
            id = in.readLong();
            associatedRecipe = in.readString();
            globalId = in.readLong();
        }

        public static final Creator<Steps> CREATOR = new Creator<Steps>() {
            @Override
            public Steps createFromParcel(Parcel in) {
                return new Steps(in);
            }

            @Override
            public Steps[] newArray(int size) {
                return new Steps[size];
            }
        };

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public long getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAssociatedRecipe() {
            return associatedRecipe;
        }

        public void setAssociatedRecipe(String associatedRecipe) {
            this.associatedRecipe = associatedRecipe;
        }

        public long getGlobalId() {
            return globalId;
        }

        public void setGlobalId(long globalId) {
            this.globalId = globalId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(thumbnail);
            dest.writeString(video);
            dest.writeString(description);
            dest.writeString(shortDescription);
            dest.writeLong(id);
            dest.writeString(associatedRecipe);
            dest.writeLong(globalId);
        }
    }

    @Entity(tableName = TABLE_INGREDIENTS)
    public static class Ingredients implements Parcelable {

        public static final String TABLE_INGREDIENTS = "ingredients";
        public static final String INGREDIENTS_ID = "id";
        public static final String INGREDIENTS_INGREDIENT = "ingredient";
        public static final String INGREDIENTS_MEASURE = "measure";
        public static final String INGREDIENTS_QUANTITY = "quantity";
        public static final String INGREDIENTS_CHECKED = "checked";
        public static final String INGREDIENTS_ASSOCIATED_RECIPE = ASSOCIATED_RECIPE;

        @ColumnInfo(name = INGREDIENTS_INGREDIENT)
        @Expose
        @SerializedName(INGREDIENTS_INGREDIENT)
        private String ingredient;

        @ColumnInfo(name = INGREDIENTS_MEASURE)
        @Expose
        @SerializedName(INGREDIENTS_MEASURE)
        private String measure;

        @ColumnInfo(name = INGREDIENTS_QUANTITY)
        @Expose
        @SerializedName(INGREDIENTS_QUANTITY)
        private double quantity;

        @ColumnInfo(name = INGREDIENTS_ASSOCIATED_RECIPE)
        private String associatedRecipe;

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(index = true, name = INGREDIENTS_ID)
        private long id;

        //not part of the api, but used to track selected ingredients for the widget
        @ColumnInfo(name = INGREDIENTS_CHECKED)
        private boolean checked = false;

        public Ingredients() {
        }

        @Ignore
        public Ingredients(long id, String ingredient, String measure, double quantity,
                           boolean checked) {
            this.id = id;
            this.ingredient = ingredient;
            this.measure = measure;
            this.quantity = quantity;
            this.checked = checked;
        }

        @Ignore
        public Ingredients(long id, String ingredient, String measure, double quantity,
                           String associatedRecipe) {
            this.id = id;
            this.ingredient = ingredient;
            this.measure = measure;
            this.quantity = quantity;
            this.associatedRecipe = associatedRecipe;
        }

        public static Ingredients fromContentValues(ContentValues values) {
            Ingredients ingredients = new Ingredients();
            if (values.containsKey(INGREDIENTS_ID)) {
                ingredients.id = values.getAsInteger(INGREDIENTS_ID);
            }
            if (values.containsKey(INGREDIENTS_INGREDIENT)) {
                ingredients.ingredient = values.getAsString(INGREDIENTS_INGREDIENT);
            }
            if (values.containsKey(INGREDIENTS_MEASURE)) {
                ingredients.measure = values.getAsString(INGREDIENTS_MEASURE);
            }
            if (values.containsKey(INGREDIENTS_QUANTITY)) {
                ingredients.quantity = values.getAsDouble(INGREDIENTS_QUANTITY);
            }
            if (values.containsKey(INGREDIENTS_CHECKED)) {
                ingredients.checked = values.getAsBoolean(INGREDIENTS_CHECKED);
            }
            if ( values.containsKey(INGREDIENTS_ASSOCIATED_RECIPE)) {
                ingredients.associatedRecipe = values.getAsString(INGREDIENTS_ASSOCIATED_RECIPE);
            }
            return ingredients;
        }

        protected Ingredients(Parcel in) {
            ingredient = in.readString();
            measure = in.readString();
            quantity = in.readDouble();
            checked = in.readByte() != 0;
            associatedRecipe = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ingredient);
            dest.writeString(measure);
            dest.writeDouble(quantity);
            dest.writeByte((byte) (checked ? 1 : 0));
            dest.writeString(associatedRecipe);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Ingredients> CREATOR = new Creator<Ingredients>() {
            @Override
            public Ingredients createFromParcel(Parcel in) {
                return new Ingredients(in);
            }

            @Override
            public Ingredients[] newArray(int size) {
                return new Ingredients[size];
            }
        };

        public String getIngredient() {
            return ingredient;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        public String getMeasure() {
            return measure;
        }

        public void setMeasure(String measure) {
            this.measure = measure;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getAssociatedRecipe() {
            return associatedRecipe;
        }

        public void setAssociatedRecipe(String associatedRecipe) {
            this.associatedRecipe = associatedRecipe;
        }

        @Override
        public String toString() {
            String measureDisplay = getMeasurementString();
            String quantityDisplay = getQuantityString();

            return quantityDisplay + " " + measureDisplay + " " + ingredient;
        }

        /**
         * remove ".0" where necessary
         *
         * @return a readable number ready for display
         */
        private String getQuantityString() {
            String quantityValue = String.valueOf(quantity);
            String quantityDisplay;
            if (quantityValue.endsWith(".0")) {
                StringTokenizer quantitySplit = new StringTokenizer(quantityValue, ".");
                quantityDisplay = quantitySplit.nextToken();
            } else {
                quantityDisplay = quantityValue;
            }
            return quantityDisplay;
        }

        /**
         * retrieve String to display readable measurement
         *
         * @return String ready to display
         */
        private String getMeasurementString() {
            HashMap<String, String> measurements = new HashMap<>();
            measurements.put("CUP", "cup");
            measurements.put("TBLSP", "tbsp");
            measurements.put("TSP", "tsp");
            measurements.put("K", "kg");
            measurements.put("G", "g");
            measurements.put("OZ", "oz");
            measurements.put("UNIT", "");

            return measurements.get(measure);
        }
    }
}