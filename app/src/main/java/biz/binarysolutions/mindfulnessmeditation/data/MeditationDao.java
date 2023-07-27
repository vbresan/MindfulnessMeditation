package biz.binarysolutions.mindfulnessmeditation.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class MeditationDao {

    @Query("SELECT COUNT(*) FROM meditation WHERE isDownloaded = 1")
    public abstract int getAllOnDeviceCount();

    @Query("SELECT title FROM meditation WHERE id = :id")
    public abstract String getTitle(String id);

    @Query("SELECT * FROM meditation WHERE isDownloaded = 1")
    public abstract LiveData<List<Meditation>> getAllOnDevice();

    @Query("SELECT * FROM meditation WHERE isDownloaded = 0")
    public abstract LiveData<List<Meditation>> getAllDownloadable();

    @Query("SELECT isDownloaded FROM meditation WHERE id = :id")
    public abstract boolean isDownloaded(String id);

    @Query("UPDATE meditation SET isTerminated = 1")
    public abstract void setAllTerminated();

    @Query("DELETE FROM meditation WHERE isDownloaded = 0 AND isTerminated = 1")
    public abstract void deleteAllTerminated();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(Meditation meditation);

    @Update
    public abstract void update(Meditation meditation);

    @Transaction
    public void refreshMeditationsList(ArrayList<Meditation> meditations ) {

        setAllTerminated();

        for (Meditation meditation : meditations) {

            meditation.setDownloaded(isDownloaded(meditation.getId()));
            insert(meditation);
        }

        deleteAllTerminated();
    }
}
