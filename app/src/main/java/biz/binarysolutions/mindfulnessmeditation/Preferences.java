package biz.binarysolutions.mindfulnessmeditation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.io.File;

import biz.binarysolutions.mindfulnessmeditation.ui.preferences.PreferenceFragment;

/**
 *
 */
public class Preferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
            .beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                    .commit();
    }

    /**
     *
     * @return
     */
    public static File getStorageDirectory(@NonNull Context context) {

        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);

        String  key        = context.getString(R.string.preferences_key_storage);
        boolean isExternal = preferences.getBoolean(key, false);

        if (isExternal) {
            return context.getExternalFilesDir(null);
        } else {
            return context.getFilesDir();
        }
    }
}
