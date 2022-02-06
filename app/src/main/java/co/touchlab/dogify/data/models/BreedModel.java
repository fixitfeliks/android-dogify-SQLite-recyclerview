package co.touchlab.dogify.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "breeds")
public class BreedModel
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="display_name")
    @SerializedName("displayName")
    public final String displayName;

    @NonNull
    @ColumnInfo(name="image_url")
    @SerializedName("imageUrl")
    public final String imageUrl;

    public BreedModel(String displayName, String imageUrl) {
        this.displayName = displayName;
        this.imageUrl = imageUrl;
    }
}
