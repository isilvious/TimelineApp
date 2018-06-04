package com.example.isaac.timeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Isaac on 3/27/2017.
 */

public class SettingFragment extends PreferenceFragment{

    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        //set the shared preferences file
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(getString(R.string.preferences_file_key));

        //Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);


    }
}
