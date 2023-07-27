package biz.binarysolutions.mindfulnessmeditation.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.util.Timestamp;

/**
 *
 */
public class Karma {

    private long timestamp;
    private int  seconds;

    /**
     *
     * @param timestamp
     * @param seconds
     */
    private Karma(long timestamp, int seconds) {
        this.timestamp = timestamp;
        this.seconds   = seconds;
    }

    /**
     *
     * @param seconds
     */
    private Karma(int seconds) {
        this(Timestamp.now(), seconds);
    }

    /**
     *
     */
    @SuppressLint("ApplySharedPref")
    private void save(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(
            context.getString(R.string.achievements_file_name),
            Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(
            context.getString(R.string.preferences_key_karma_timestamp),
            Timestamp.now()
        );
        editor.putInt(
            context.getString(R.string.preferences_key_karma_seconds),
            seconds
        );

        editor.commit();
    }

    /**
     *
     * @param context
     * @return
     */
    private static Karma getSaved(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(
            context.getString(R.string.achievements_file_name),
            Context.MODE_PRIVATE
        );

        long timestamp = preferences.getLong(
            context.getString(R.string.preferences_key_karma_timestamp),
            -1
        );
        int seconds = preferences.getInt(
            context.getString(R.string.preferences_key_karma_seconds),
            -1
        );

        if (timestamp == -1 || seconds == -1 ) {
            return null;
        }

        return new Karma(timestamp, seconds);
    }

    /**
     *
     * @param duration
     * @return
     */
    private static int calculateSeconds(String duration) {

        String[] parts = duration.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     *
     * @return
     */
    private static Karma getCalculated(Context context) {

        CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(context).completedMeditationDao();

        List<CompletedMeditation> meditations = dao.getAllEntities();
        if (meditations == null) {
            return new Karma(0);
        }

        int seconds = 0;
        for (CompletedMeditation meditation: meditations) {

            String id       = meditation.getMeditationId();
            String duration = dao.getDuration(id);

            seconds += calculateSeconds(duration);
        }

        return new Karma(seconds);
    }

    /**
     *
     * @param context
     */
    private void update(Context context) {

        CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(context).completedMeditationDao();

        List<CompletedMeditation> meditations = dao.getNewEntities(timestamp);
        if (meditations == null) {
            return;
        }

        for (CompletedMeditation meditation: meditations) {

            String id       = meditation.getMeditationId();
            String duration = dao.getDuration(id);

            seconds += calculateSeconds(duration);
        }

        timestamp = Timestamp.now();
    }

    /**
     *
     * @param context
     * @return
     */
    public static Karma get(Context context) {

        Karma karma = getSaved(context);
        if (karma == null) {
            karma = getCalculated(context);
        }

        karma.update(context);
        karma.save(context);

        return karma;
    }

    /**
     *
     * @return
     */
    public int getCount() {
        return seconds / 60;
    }
}
