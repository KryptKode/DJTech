package com.kryptkode.cyberman.djtech;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SettingsActivityFragment settingsFragment = new SettingsActivityFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.root_settings, settingsFragment,  null).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();



    }


}
