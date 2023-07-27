package biz.binarysolutions.mindfulnessmeditation.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CompletedMeditationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(CompletedMeditation meditation);

    @Query("SELECT completedmeditation.timeStamp AS timeStamp, meditation.title AS meditationId " +
            "FROM completedmeditation, meditation " +
            "WHERE completedmeditation.meditationId = meditation.id " +
            "ORDER BY completedmeditation.timeStamp")
    LiveData<List<CompletedMeditation>> getAll();

    @Query("SELECT COUNT(*) FROM completedmeditation")
    int getCount();

    @Query("SELECT COUNT(*) FROM completedmeditation " +
            "WHERE timeStamp >= :timeStampFrom " +
            "AND timeStamp < :timeStampTo")
    int getCount(long timeStampFrom, long timeStampTo);

    @Query("SELECT * FROM completedmeditation")
    List<CompletedMeditation> getAllEntities();

    @Query("SELECT * FROM completedmeditation " +
            "WHERE timeStamp >= :timestamp")
    List<CompletedMeditation> getNewEntities(long timestamp);

    @Query("SELECT duration FROM meditation WHERE meditation.id = :id")
    String getDuration(String id);
}
