package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Family Map: Settings");

        if (findViewById(R.id.idFrame) != null){
            if (savedInstanceState != null){
                return;
            }

            getFragmentManager().beginTransaction().add(R.id.idFrame, new SettingsFragment()).commit();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
    public static class SettingsFragment extends PreferenceFragment {
        Preference lifeStory = (Preference) findPreference("life_story");

        @Override
        public void onCreate( Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // below line is used to add preference
            // fragment from our xml folder.
            addPreferencesFromResource(R.xml.prefrences);
            DataCache data = DataCache.getInstance();
            final Preference lifeStory = findPreference("life_story");
            final Preference familyTree = findPreference("family_tree");
            final Preference spouseLine = findPreference("spouse_line");
            final Preference fatherSide = findPreference("father_side");
            final Preference motherSide = findPreference("mother_side");
            final Preference eventMale = findPreference("male_event");
            final Preference eventFemale = findPreference("female_event");
            final Preference logout = findPreference("logout_code");

            lifeStory.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setLifeStory(true);
                    }
                    else {
                        data.setLifeStory(false);
                    }
                    return true;
                }
            });

            familyTree.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setFamilyTreeLine(true);
                    }
                    else {
                        data.setFamilyTreeLine(false);
                    }
                    return true;
                }
            });

            spouseLine.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setSpouseLine(true);
                    }
                    else {
                        data.setSpouseLine(false);
                    }
                    return true;
                }
            });

            fatherSide.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setFatherSide(true);
                    }
                    else {
                        data.setFatherSide(false);
                    }
                    return true;
                }
            });

            motherSide.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setMotherSide(true);
                    }
                    else {
                        data.setMotherSide(false);
                    }
                    return true;
                }
            });

            eventMale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setMaleEvents(true);
                    }
                    else {
                        data.setMaleEvents(false);
                    }
                    return true;
                }
            });

            eventFemale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals(true)) {
                        data.setFemaleEvents(true);
                    }
                    else {
                        data.setFemaleEvents(false);
                    }
                    return true;
                }
            });

            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
            });

        }


    }

}

