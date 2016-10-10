package com.pelsoczi.vendship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
        setContentView(R.layout.activity_vendors);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mTwoPane = findViewById(R.id.container_detail) != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Yelp.SEARCH_YELP_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(Yelp.KEY_YELP)) {
                VendorsFragment vendors = (VendorsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.container_vendor);
                vendors.searchYelp(data.getStringExtra(Yelp.KEY_YELP));
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
        if (mTwoPane) {
            DetailFragment detail = (DetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_detail);

            if (detail == null || detail.getShownIndex() != index) {
                detail = DetailFragment.newInstance(business, index);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_detail, detail, DetailFragment.TAG)
                        .addToBackStack(DetailFragment.TAG)
                        .commit();
            }
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Business.class.getSimpleName(), business)
                    .putExtra(DetailFragment.VENDOR_INDEX, index);
            startActivity(intent);
        }
    }

    public void loadDetails(Business business, int index) {
        if (mTwoPane) {
            DetailFragment detail = DetailFragment.newInstance(business, index);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_detail, detail, DetailFragment.TAG)
                    .commitAllowingStateLoss();
        }
    }

    public void doDataUpdated() {
        VendorsFragment vendors = (VendorsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container_vendor);
        vendors.doDataUpdated();

        getSupportFragmentManager().popBackStackImmediate(DetailFragment.TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}