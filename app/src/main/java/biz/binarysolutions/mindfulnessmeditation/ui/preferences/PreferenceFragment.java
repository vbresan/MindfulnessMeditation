package biz.binarysolutions.mindfulnessmeditation.ui.preferences;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import biz.binarysolutions.mindfulnessmeditation.R;

/**
 *
 */
public class PreferenceFragment
    extends    PreferenceFragmentCompat
    implements OnPreferenceChangeListener {

    /**
     *
     */
    private void setStoragePreference() {

        Context context = getContext();
        if (context == null) {
            return;
        }

        if (context.getExternalFilesDir(null) == null) {
            return;
        }

        Preference preference = findPreference(getString(R.string.preferences_key_storage));
        if (preference == null) {
            return;
        }

        preference.setEnabled(true);
        preference.setOnPreferenceChangeListener(this);
    }

    /**
     *
     * @return
     * @param size
     */
    private MoveFilesDialogFragment showProgressDialog(int size) {

        FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }

        MoveFilesDialogFragment fragment = new MoveFilesDialogFragment(size);
        fragment.show(activity.getSupportFragmentManager(), "");

        return fragment;
    }


    /**
     *
     * @param files
     * @return
     */
    private int getTotalSize(File[] files) {

        int totalSize = 0;

        for (File file: files) {
            totalSize += file.length();
        }

        return totalSize;
    }

    /**
     *
     * @param from
     * @param to
     */
    private void copyFile(File from, File to) {

        try {
            InputStream  in  = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);

            byte[] buffer = new byte[4 * 1024];
            int    bytesRead;

            while ((bytesRead = in.read(buffer)) > 0 ) {
                out.write(buffer, 0, bytesRead);
            }

            in.close();
            out.close();

        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     *
     * @param file
     */
    private void deleteFile(File file) {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    /**
     *
     * @param fromDirectory
     * @param toDirectory
     * @param files
     */
    private void moveFiles
        (
            final File   fromDirectory,
            final File   toDirectory,
            final File[] files
        ) {

        int totalSize = getTotalSize(files);
        MoveFilesDialogFragment dialog = showProgressDialog(totalSize);

        for (File file: files) {

            String fileName = file.getName();
            long   fileSize = file.length();

            File   fromFile = new File(fromDirectory, fileName);
            File   toFile   = new File(toDirectory,   fileName);

            copyFile(fromFile, toFile);
            deleteFile(fromFile);

            if (dialog != null) {
                dialog.publishProgress((int) fileSize);
            }
        }

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     *
     * @param toExternalStorage
     */
    private void moveFiles(boolean toExternalStorage) {

        Context context = getContext();
        if (context == null) {
            return;
        }

        File from;
        File to;

        if (toExternalStorage) {
            from = context.getFilesDir();
            to   = context.getExternalFilesDir(null);
        } else {
            from = context.getExternalFilesDir(null);
            to   = context.getFilesDir();
        }

        File[] files = from.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        moveFiles(from, to, files);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle bundle, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setStoragePreference();
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, final Object newValue) {

        String key = preference.getKey();
        if (key.equals(getString(R.string.preferences_key_storage))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    moveFiles((boolean) newValue);
                }
            }).start();
        }

        return true;
    }
}
