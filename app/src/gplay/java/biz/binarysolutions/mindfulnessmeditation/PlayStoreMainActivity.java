package biz.binarysolutions.mindfulnessmeditation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import biz.binarysolutions.mindfulnessmeditation.data.CompletedMeditationDao;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;
import biz.binarysolutions.mindfulnessmeditation.util.Timestamp;

/**
 *
 */
public class PlayStoreMainActivity extends MainActivity {

    private static final long MILLIS_IN_MONTH = 1000L * 60 * 60 * 24 * 30;

    /**
     *
     */
    @SuppressLint("ApplySharedPref")
    private void saveReviewRequestTimestamp() {

        SharedPreferences preferences = getSharedPreferences(
            getString(R.string.review_request_file_name),
            Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(
            getString(R.string.preferences_key_review_request_timestamp),
            Timestamp.now()
        );

        editor.commit();
    }

    /**
     *
     * @return
     */
    private long getReviewRequestTimestamp() {

        SharedPreferences preferences = getSharedPreferences(
            getString(R.string.review_request_file_name),
            Context.MODE_PRIVATE
        );

        return preferences.getLong(
            getString(R.string.preferences_key_review_request_timestamp), -1
        );
    }

    /**
     *
     * @param requestTimestamp
     * @return
     */
    private boolean isNeverRequested(long requestTimestamp) {
        return requestTimestamp < 0;
    }

    /**
     *
     * @param requestTimestamp
     * @return
     */
    private boolean isRequestedMoreThanMonthAgo(long requestTimestamp) {
        return (Timestamp.now() - requestTimestamp) > MILLIS_IN_MONTH;
    }

    /**
     *
     * @return
     */
    private boolean isAppInstalledMoreThanTwoWeeksAgo() {

        long installTimestamp = 0;

        try {
            installTimestamp =
                getPackageManager().
                    getPackageInfo(getPackageName(), 0).
                        firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }

        return (Timestamp.now() - installTimestamp) > MILLIS_IN_MONTH / 2;
    }

    /**
     *
     * @return
     */
    private int getNumberOfCompletedMeditations() {

        CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(this).completedMeditationDao();

        return dao.getCount();
    }

    /**
     *
     * @return
     */
    private int getNumberOfCompletedMeditationsToday() {

        CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(this).completedMeditationDao();

        long from = Timestamp.today();
        long to   = Timestamp.now();

        return dao.getCount(from, to);
    }

    /**
     *
     */
    private void requestReview() {

        ReviewManager    manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(requestTask -> {
            if (requestTask.isSuccessful()) {

                ReviewInfo reviewInfo = requestTask.getResult();
                if (reviewInfo != null) {
                    Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                    flow.addOnCompleteListener(flowTask -> saveReviewRequestTimestamp());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(() -> {

            long timestamp = getReviewRequestTimestamp();
            if (isNeverRequested(timestamp)) {
                if (isAppInstalledMoreThanTwoWeeksAgo()) {
                    if (getNumberOfCompletedMeditations() >= 5) {
                        if (getNumberOfCompletedMeditationsToday() >= 1) {
                            requestReview();
                        }
                    }
                }
            } else if (isRequestedMoreThanMonthAgo(timestamp)){
                requestReview();
            }

        }).start();
    }
}
