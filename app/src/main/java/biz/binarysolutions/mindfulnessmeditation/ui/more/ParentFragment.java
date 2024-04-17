package biz.binarysolutions.mindfulnessmeditation.ui.more;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;

import biz.binarysolutions.mindfulnessmeditation.Credits;
import biz.binarysolutions.mindfulnessmeditation.Preferences;
import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.ui.preferences.PreferenceFragment;

/**
 *
 */
public class ParentFragment extends PreferenceFragment {

    /**
     *
     */
    private void setCreditsOnClickListener() {

        String     key        = getString(R.string.more_key_credits);
        Preference preference = findPreference(key);

        if (preference == null) {
            return;
        }

        preference.setOnPreferenceClickListener(p -> {

            Intent intent = new Intent(getActivity(), Credits.class);
            startActivity(intent);
            return true;
        });
    }

    /**
     *
     */
    private void setPreferencesOnClickListener() {

        String     key        = getString(R.string.more_key_preferences);
        Preference preference = findPreference(key);

        if (preference == null) {
            return;
        }

        preference.setOnPreferenceClickListener(p -> {

            Intent intent = new Intent(getActivity(), Preferences.class);
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle bundle, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.more, rootKey);

        setCreditsOnClickListener();
        setPreferencesOnClickListener();
    }
}
