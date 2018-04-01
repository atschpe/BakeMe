package com.example.android.bakeme.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Retrofit;

/**
 * {@link Recipe} is an object holding the various infos provided by the API concerning all
 * aspects of the recipe in question. It is setup to using http://www.jsonschema2pojo.org/
 * to enable {@link Retrofit} to use it and implements {@link Parcelable} to enable
 * data persistence.
 */
public class Recipe implements Parcelable {

    private static final String ASSOCIATED_RECIPE = "associatedRecipe";

    // api keys
    private static final String RECIPE_ID = "id";
    private static final String RECIPE_IMAGE = "image";
    private static final String RECIPE_SERVINGS = "servings";
    private static final String RECIPE_STEPS = "steps";
    private static final String RECIPE_INGREDIENTS = "ingredients";
    private static final String RECIPE_NAME = "name";

    @Expose
    @SerializedName(RECIPE_IMAGE)
    private String image;

    @Expose
    @SerializedName(RECIPE_SERVINGS)
    private int servings;

    @Expose
    @SerializedName(RECIPE_STEPS)
    private List<Steps> steps;

    @Expose
    @SerializedName(RECIPE_INGREDIENTS)
    private List<Ingredients> ingredients;

    @Expose
    @SerializedName(RECIPE_NAME)
    private String name;

    @Expose
    @SerializedName(RECIPE_ID)
    private long id;

    private boolean favourited = false;

    public Recipe(int id, String image, String name, int servings, boolean favourited) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.servings = servings;
        this.favourited = favourited;
    }

    private Recipe(Parcel in) {
        image = in.readString();
        servings = in.readInt();
        name = in.readString();
        id = in.readInt();
        favourited = in.readByte() != 0;

        //inner classes
        ingredients = new ArrayList<>();
        in.readList(ingredients, Ingredients.class.getClassLoader());

        steps = new ArrayList<>();
        in.readList(steps, Steps.class.getClassLoader());
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

    public int getServings() {
        return servings;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
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

    public static class Steps implements Parcelable {

        public static final String STEPS_ID = "id";
        public static final String STEPS_THUMB = "thumbnailURL";
        public static final String STEPS_VIDEO = "videoURL";
        public static final String STEPS_DESCRIP = "description";
        public static final String STEPS_SHORT_DESCRIP = "shortDescription";
        public static final String STEPS_ASSOCIATED_RECIPE = ASSOCIATED_RECIPE ;

        @Expose
        @SerializedName(STEPS_THUMB)
        private String thumbnail;

        @Expose
        @SerializedName(STEPS_VIDEO)
        private String video;

        @Expose
        @SerializedName(STEPS_DESCRIP)
        private String description;

        @Expose
        @SerializedName(STEPS_SHORT_DESCRIP)
        private String shortDescription;

        private String associatedRecipe;

        @Expose
        @SerializedName(STEPS_ID)
        private long id;

        private long globalId;


        public Steps() {
        }

        public Steps(long id, String shortDescription, String description, String video,
                     String thumbnail) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.video = video;
            this.thumbnail = thumbnail;
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

        public void setId(long id) {
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

    public static class Ingredients implements Parcelable {

        static final String INGREDIENTS_INGREDIENT = "ingredient";
        static final String INGREDIENTS_MEASURE = "measure";
        static final String INGREDIENTS_QUANTITY = "quantity";

        @Expose
        @SerializedName(INGREDIENTS_INGREDIENT)
        private String ingredient;

        @Expose
        @SerializedName(INGREDIENTS_MEASURE)
        private String measure;

        @Expose
        @SerializedName(INGREDIENTS_QUANTITY)
        private double quantity;

        private String associatedRecipe;

        private long id;

        private boolean checked = false;

        public Ingredients(long id, String ingredient, String measure, double quantity,
                           boolean checked) {
            this.id = id;
            this.ingredient = ingredient;
            this.measure = measure;
            this.quantity = quantity;
            this.checked = checked;
        }

        public Ingredients(long id, String ingredient, String measure, double quantity,
                           String associatedRecipe) {
            this.id = id;
            this.ingredient = ingredient;
            this.measure = measure;
            this.quantity = quantity;
            this.associatedRecipe = associatedRecipe;
        }

        Ingredients(Parcel in) {
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

        public String getMeasure() {
            return measure;
        }

        public double getQuantity() {
            return quantity;
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