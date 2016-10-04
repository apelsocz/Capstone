package com.pelsoczi.vendship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pelsoczi.vendship.ui.DetailFragment;
import com.pelsoczi.vendship.ui.VendorsFragment;
import com.pelsoczi.vendship.util.Yelp;
import com.yelp.clientlib.entities.Business;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Yelp.SEARCH_YELP_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(Yelp.KEY_YELP)) {
                VendorsFragment fragment = (VendorsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.container_vendor);
                fragment.searchYelp(data.getStringExtra(Yelp.KEY_YELP));
                //// TODO: 16-10-02 Adapter onBindViewHolder, search additional by offset
            }
        }
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

    public void showSearch(String jsonAsString) {
        Intent intent = new Intent(getBaseContext(), SearchActivity.class);
        if (jsonAsString != null && !jsonAsString.isEmpty()) {
            intent.putExtra(Yelp.KEY_YELP, jsonAsString);
        }
        startActivityForResult(intent, Yelp.SEARCH_YELP_REQUEST);
    }

    public void showDetails(Business business, int index) {
        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra(Business.class.getSimpleName(), business)
                .putExtra(DetailFragment.VENDOR_INDEX, index);
        startActivity(intent);
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