package biz.binarysolutions.mindfulnessmeditation.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Meditation.class, CompletedMeditation.class}, version = 1)
public abstract class MeditationDatabase extends RoomDatabase {

    private static volatile MeditationDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public  static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     *
     * @param context context
     * @return database
     */
    public static MeditationDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (MeditationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        MeditationDatabase.class, "meditation_db"
                    ).build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract MeditationDao          meditationDao();
    public abstract CompletedMeditationDao completedMeditationDao();
}
