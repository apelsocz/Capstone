package com.pelsoczi.vendship;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.container_settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        public static final String TAG = SettingsFragment.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_distance_key)));
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
            else {
                preference.setSummary(stringValue);
            }

            return true;
        }
    }
}
