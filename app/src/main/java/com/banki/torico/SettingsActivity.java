package com.banki.torico;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String[] mAutoSummaryFields = { "salario_bruto", "horas_mensais", "adicional_extra" };
    private final int mEntryCount = mAutoSummaryFields.length;
    private CustomEditPreference[] mPreferenceEntries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mPreferenceEntries = new CustomEditPreference[mEntryCount];
        for (int i = 0; i < mEntryCount; i++) {
            mPreferenceEntries[i] = (CustomEditPreference)getPreferenceScreen().findPreference(mAutoSummaryFields[i]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mEntryCount; i++) {
            updateSummary(mAutoSummaryFields[i]);
        }
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateSummary(String key) {
        for (int i = 0; i < mEntryCount; i++) {
            if (key.equals(mAutoSummaryFields[i])) {
                mPreferenceEntries[i].updateSummary();
                break;
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(key);
    }
}
