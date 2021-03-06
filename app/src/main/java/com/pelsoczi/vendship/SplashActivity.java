package com.pelsoczi.vendship;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Intent intent = new Intent(this, VendorActivity.class);
        startActivity(intent);
        supportFinishAfterTransition();
    }
}