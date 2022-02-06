package co.touchlab.dogify.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import co.touchlab.dogify.data.models.BreedModel;

@Database(entities = {BreedModel.class}, version = 2)
public abstract class DogifyDb extends RoomDatabase
{
    private static DogifyDb INSTANCE;
    public abstract BreedDao breedDao();

    public static synchronized DogifyDb getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context, DogifyDb.class, "breeds_list")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
