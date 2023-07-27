package biz.binarysolutions.mindfulnessmeditation.ui.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import biz.binarysolutions.mindfulnessmeditation.R;

/**
 *
 */
public class MoveFilesDialogFragment extends DialogFragment {

    private ProgressBar progressBar;

    private final int max;
    private int totalProgress;

    /**
     *
     * @param max
     */
    public MoveFilesDialogFragment(int max) {
        super();
        this.max = max;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_move, null);

        progressBar = root.findViewById(R.id.progressBarTotal);
        progressBar.setMax(max);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(root);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return dialog;
    }

    /**
     *
     * @param progress
     */
    public void publishProgress(int progress) {

        totalProgress += progress;

        if (progressBar != null) {
            progressBar.setProgress(totalProgress);
        }
    }
}
