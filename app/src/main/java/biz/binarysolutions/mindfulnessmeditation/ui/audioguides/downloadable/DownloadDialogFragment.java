package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.util.List;

import biz.binarysolutions.mindfulnessmeditation.Preferences;
import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDao;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;
import biz.binarysolutions.mindfulnessmeditation.network.BinaryDownloader;
import biz.binarysolutions.mindfulnessmeditation.network.DownloadCallback;

/**
 *
 */
public class DownloadDialogFragment extends DialogFragment
    implements
        DialogInterface.OnClickListener,
        DownloadCallback<Integer> {

    private View        root;
    private TextView    textViewCurrentTitle;
    private ProgressBar progressBarCurrent;
    private ProgressBar progressBarTotal;

    private List<Meditation> meditations;

    private int totalSize      = 0;
    private int downloadedSize = 0;

    private BinaryDownloader downloader;

    /**
     *
     * @param meditations
     */
    private void setTotalSize(List<Meditation> meditations) {
        for (Meditation meditation : meditations) {
            totalSize += meditation.getSize();
        }

        if (progressBarTotal != null) {
            progressBarTotal.setMax(totalSize);
        }
    }

    /**
     *
     */
    private void downloadNextMeditation() {

        Meditation meditation = meditations.get(0);

        if (textViewCurrentTitle != null) {
            textViewCurrentTitle.setText(meditation.getTitle());
        }

        if (progressBarCurrent != null) {
            progressBarCurrent.setProgress(0);
            progressBarCurrent.setMax((int) meditation.getSize());
        }

        Context context = getContext();
        if (context == null) {
            return;
        }

        String url       = meditation.getUrl();
        File   directory = Preferences.getStorageDirectory(context);
        String fileName  = meditation.getId() + ".mp3";

        downloader = new BinaryDownloader();
        downloader.startDownload(this, url, directory, fileName);
    }

    /**
     *
     */
    private void startDownload() {

        setTotalSize(meditations);
        downloadNextMeditation();
    }

    /**
     *
     * @param progress
     */
    private void updateProgressBars(int progress) {

        if (progressBarCurrent != null) {
            progressBarCurrent.setProgress(progress);
        }

        if (progressBarTotal != null) {
            progressBarTotal.setProgress(downloadedSize + progress);
        }
    }

    /**
     *
     * @param meditation
     */
    private void updateDatabase(final Meditation meditation) {

        final MeditationDao dao =
            MeditationDatabase.getDatabase(getActivity().getApplicationContext())
                .meditationDao();

        MeditationDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.update(meditation);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        root = inflater.inflate(R.layout.dialog_download, null);

        textViewCurrentTitle = root.findViewById(R.id.textViewCurrentTitle);
        progressBarCurrent   = root.findViewById(R.id.progressBarCurrent);
        progressBarTotal     = root.findViewById(R.id.progressBarTotal);

        setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(root);
        builder.setNegativeButton(android.R.string.cancel, this);

        if (meditations != null) {
            startDownload();
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int i) {

        if (downloader == null) {
            return;
        }

        downloader.cancelDownload();
        downloader = null;
    }

    /**
     *
     * @param meditations
     */
    public void downloadMeditations(List<Meditation> meditations) {

        this.meditations = meditations;

        if (root != null) {
            startDownload();
        }
    }

    @Override
    public void onProgressUpdate(int code, int size) {

        switch(code) {
            case Progress.ERROR:
                System.out.println("ERROR");
                break;
            case Progress.CONNECT_SUCCESS:
                System.out.println("CONNECT_SUCCESS");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                System.out.println("GET_INPUT_STREAM_SUCCESS");
                break;
            case Progress.DOWNLOAD_INPUT_STREAM_IN_PROGRESS:
                updateProgressBars(size);
                System.out.println("DOWNLOAD_INPUT_STREAM_IN_PROGRESS");
                break;
            case Progress.DOWNLOAD_INPUT_STREAM_SUCCESS:
                System.out.println("DOWNLOAD_INPUT_STREAM_SUCCESS");
                break;
        }
    }

    @Override
    public void onDownloadFinished(Integer size) {

        Meditation meditation = meditations.get(0);
        if (size == meditation.getSize()) {

            meditation.setDownloaded(true);
            updateDatabase(meditation);

            downloadedSize += size;
        }

        meditations.remove(0);
        if (meditations.size() > 0) {
            downloadNextMeditation();
        } else {
            dismiss();
        }
    }

    @Override
    public void onDownloadFailed() {
        dismiss();
    }
}
