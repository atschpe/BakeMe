package com.example.android.bakeme.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeReworked implements Parcelable {

    public static final Creator<RecipeReworked> CREATOR = new Creator<RecipeReworked>() {
        @Override
        public RecipeReworked createFromParcel(Parcel in) {
            return new RecipeReworked(in);
        }

        @Override
        public RecipeReworked[] newArray(int size) {
            return new RecipeReworked[size];
        }
    };
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

    public RecipeReworked(String image, int servings, List<Steps> steps,
                          List<Ingredients> ingredients, String name, long id, boolean favourited) {
        this.image = image;
        this.servings = servings;
        this.steps = steps;
        this.ingredients = ingredients;
        this.name = name;
        this.id = id;
        this.favourited = favourited;
    }

    protected RecipeReworked(Parcel in) {
        image = in.readString();
        servings = in.readInt();
        name = in.readString();
        id = in.readLong();
        favourited = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

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

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public static class Steps implements Parcelable {

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
        private static final String STEPS_ID = "id";
        private static final String STEPS_THUMB = "thumbnailURL";
        private static final String STEPS_VIDEO = "videoURL";
        private static final String STEPS_DESCRIP = "description";
        private static final String STEPS_SHORT_DESCRIP = "shortDescription";
        @Expose
        @SerializedName(STEPS_THUMB)
        private String thumbnailurl;
        @Expose
        @SerializedName(STEPS_VIDEO)
        private String videourl;
        @Expose
        @SerializedName(STEPS_DESCRIP)
        private String description;
        @Expose
        @SerializedName(STEPS_SHORT_DESCRIP)
        private String shortdescription;

        @Expose
        @SerializedName(STEPS_ID)
        private int id;

        private long globalId;

        private String associatedRecipe;

        public Steps(String thumbnailurl, String videourl, String description,
                     String shortdescription, int id, long globalId, String associatedRecipe) {
            this.thumbnailurl = thumbnailurl;
            this.videourl = videourl;
            this.description = description;
            this.shortdescription = shortdescription;
            this.id = id;
            this.globalId = globalId;
            this.associatedRecipe = associatedRecipe;
        }

        protected Steps(Parcel in) {
            thumbnailurl = in.readString();
            videourl = in.readString();
            description = in.readString();
            shortdescription = in.readString();
            id = in.readInt();
            globalId = in.readLong();
            associatedRecipe = in.readString();
        }

        public String getThumbnailurl() {
            return thumbnailurl;
        }

        public void setThumbnailurl(String thumbnailurl) {
            this.thumbnailurl = thumbnailurl;
        }

        public String getVideourl() {
            return videourl;
        }

        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getShortdescription() {
            return shortdescription;
        }

        public void setShortdescription(String shortdescription) {
            this.shortdescription = shortdescription;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getGlobalId() {
            return globalId;
        }

        public void setGlobalId(long globalId) {
            this.globalId = globalId;
        }

        public String getAssociatedRecipe() {
            return associatedRecipe;
        }

        public void setAssociatedRecipe(String associatedRecipe) {
            this.associatedRecipe = associatedRecipe;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(thumbnailurl);
            dest.writeString(videourl);
            dest.writeString(description);
            dest.writeString(shortdescription);
            dest.writeInt(id);
            dest.writeLong(globalId);
            dest.writeString(associatedRecipe);
        }
    }

    public static class Ingredients implements Parcelable {

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
        private static final String INGREDIENTS_INGREDIENT = "ingredient";
        private static final String INGREDIENTS_MEASURE = "measure";
        private static final String INGREDIENTS_QUANTITY = "quantity";
        @Expose
        @SerializedName(INGREDIENTS_INGREDIENT)
        private String ingredient;
        @Expose
        @SerializedName(INGREDIENTS_MEASURE)
        private String measure;
        @Expose
        @SerializedName(INGREDIENTS_QUANTITY)
        private int quantity;
        private String associatedRecipe;
        private long id;
        private boolean checked = false;
        public Ingredients(String ingredient, String measure, int quantity, String associatedRecipe,
                           long id, boolean checked) {
            this.ingredient = ingredient;
            this.measure = measure;
            this.quantity = quantity;
            this.associatedRecipe = associatedRecipe;
            this.id = id;
            this.checked = checked;
        }

        protected Ingredients(Parcel in) {
            ingredient = in.readString();
            measure = in.readString();
            quantity = in.readInt();
            checked = in.readByte() != 0;
            associatedRecipe = in.readString();
        }

        public String getAssociatedRecipe() {
            return associatedRecipe;
        }

        public void setAssociatedRecipe(String associatedRecipe) {
            this.associatedRecipe = associatedRecipe;
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

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ingredient);
            dest.writeString(measure);
            dest.writeInt(quantity);
            dest.writeByte((byte) (checked ? 1 : 0));
            dest.writeString(associatedRecipe);
        }
    }
}
