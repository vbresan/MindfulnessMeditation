package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.ondevice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;

import biz.binarysolutions.mindfulnessmeditation.Preferences;
import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDao;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;

/**
 *
 */
public class DeleteMeditationDialogFragment extends DialogFragment
    implements DialogInterface.OnClickListener {

    private final Meditation meditation;

    /**
     *
     * @param meditation
     */
    public DeleteMeditationDialogFragment(Meditation meditation) {
        this.meditation = meditation;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setMessage(R.string.ConfirmDelete);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        Context context = getContext();
        if (context == null) {
            return;
        }

        meditation.setDownloaded(false);

        final MeditationDao dao =
            MeditationDatabase.getDatabase(context)
                .meditationDao();

        MeditationDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.update(meditation);
            }
        });

        File   directory = Preferences.getStorageDirectory(context);
        String fileName  = meditation.getId() + ".mp3";

        //noinspection ResultOfMethodCallIgnored
        new File(directory, fileName).delete();
    }
}
