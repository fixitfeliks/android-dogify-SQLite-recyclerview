package co.touchlab.dogify.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import co.touchlab.dogify.data.models.BreedModel;

@Dao
public interface BreedDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBreeds(List<BreedModel> breeds) throws Exception;

    @Query("SELECT * FROM breeds")
    List<BreedModel> getAllBreeds() throws Exception;
}
