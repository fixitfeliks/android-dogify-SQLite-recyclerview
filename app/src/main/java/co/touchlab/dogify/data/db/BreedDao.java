package co.touchlab.dogify.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BreedDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBreeds(List<BreedModelEntity> breeds);

    @Query("SELECT * FROM breeds")
    List<BreedModelEntity> getAllBreeds();
}
