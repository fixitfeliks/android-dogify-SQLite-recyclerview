package co.touchlab.dogify.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "breeds")

public class BreedModelEntity
{
        @PrimaryKey
        @NonNull
        @ColumnInfo(name="display_name")
        @SerializedName("displayName")
        public String displayName;

        @NonNull
        @ColumnInfo(name="image_url")
        @SerializedName("imageUrl")
        public String imageUrl;

        public BreedModelEntity(String displayName, String imageUrl) {
                this.displayName = displayName;
                this.imageUrl = imageUrl;
        }
}