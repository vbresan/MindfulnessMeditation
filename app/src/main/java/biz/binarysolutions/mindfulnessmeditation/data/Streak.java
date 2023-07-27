package biz.binarysolutions.mindfulnessmeditation.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.util.Timestamp;

/**
 *
 */
public class Streak {

    private static final long DAY_OFFSET_MILLIS = 24 * 60 * 60 * 1000;

    private long date;
    private int  count;

    /**
     *
     * @param date
     * @param count
     */
    private Streak(long date, int count) {

        this.date  = date;
        this.count = count;
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
        editor.putLong(context.getString(R.string.preferences_key_streak_date), date);
        editor.putInt(context.getString(R.string.preferences_key_streak_count), count);
        editor.commit();
    }

    /**
     *
     * @param context
     * @return
     */
    private static Streak getSaved(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(
            context.getString(R.string.achievements_file_name),
            Context.MODE_PRIVATE
        );

        long date = preferences.getLong(
            context.getString(R.string.preferences_key_streak_date),
            -1
        );

        int count = preferences.getInt(
            context.getString(R.string.preferences_key_streak_count),
            -1
        );

        if (date == -1 || count == -1) {
            return null;
        }

        return new Streak(date, count);
    }

    /**
     *
     * @return
     */
    private static Streak getCalculated(Context context) {

        CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(context).completedMeditationDao();

        long today     = Timestamp.today();
        long dayBefore = today - DAY_OFFSET_MILLIS;

        long date  = dayBefore;
        int  count = 0;

        while (dao.getCount(dayBefore, dayBefore + DAY_OFFSET_MILLIS) > 0) {
            count++;
            dayBefore -= DAY_OFFSET_MILLIS;
        }

        return new Streak(date, count);
    }

    /**
     *
     * @param context
     */
    private void updateForToday(Context context) {

        CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(context).completedMeditationDao();

        long today     = Timestamp.today();
        long yesterday = today - DAY_OFFSET_MILLIS;

        if (date < yesterday) {
            count = 0;
        }

        if (dao.getCount(today, today + DAY_OFFSET_MILLIS) > 0 && date < today) {
            count++;
            date = today;
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static Streak get(Context context) {

        Streak streak = getSaved(context);
        if (streak == null) {
            streak = getCalculated(context);
        }

        streak.updateForToday(context);
        streak.save(context);

        return streak;
    }

    /**
     *
     * @return
     */
    public boolean isUpdatedToday() {
        return date == Timestamp.today();
    }

    /**
     *
     * @return
     */
    public int getCount() {
        return count;
    }
}
