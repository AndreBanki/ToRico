package com.banki.torico.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.banki.torico.R;
import com.banki.torico.settings.CustomEditPreference;

public class SettingsFragment extends PreferenceFragment  implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String[] mAutoSummaryFields = { "salario_bruto", "horas_mensais", "adicional_extra" };
    private final int mEntryCount = mAutoSummaryFields.length;
    private CustomEditPreference[] mPreferenceEntries;

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPreferenceEntries = new CustomEditPreference[mEntryCount];
        for (int i = 0; i < mEntryCount; i++) {
            mPreferenceEntries[i] = (CustomEditPreference)getPreferenceScreen().findPreference(mAutoSummaryFields[i]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < mEntryCount; i++) {
            updateSummary(mAutoSummaryFields[i]);
        }
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(key);
    }

    private void updateSummary(String key) {
        for (int i = 0; i < mEntryCount; i++) {
            if (key.equals(mAutoSummaryFields[i])) {
                mPreferenceEntries[i].updateSummary();
                break;
            }
        }
    }
}
