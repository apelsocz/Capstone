package com.pelsoczi.vendship;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class VendorActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

//        mTwoPane = findViewById(R.id.container_detail) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return false;
    }

//    public updateUI() {
//        String title = getResources().getString(R.string.app_name);
//        String subtitle = Utility.getSortLabel(this);
//        int iconResId = Utility.getSortIconResId(this);
//        mToolbar.setTitle(title);
//        mToolbar.setSubtitle(subtitle);
//        mToolbar.setLogo(iconResId);
//    }
}