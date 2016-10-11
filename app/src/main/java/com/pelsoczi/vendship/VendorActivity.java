package com.pelsoczi.vendship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.pelsoczi.vendship.ui.DetailFragment;
import com.pelsoczi.vendship.ui.VendorsFragment;
import com.pelsoczi.vendship.util.Yelp;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

public class VendorActivity extends AppCompatActivity {

    private static final String LOG_TAG = VendorActivity.class.getSimpleName();

    private boolean mTwoPane;
    private Toolbar mToolbar;
    private String dataSet = "";

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
        Log.i(LOG_TAG, "showDetails");
        if (mTwoPane) {
            DetailFragment detail = (DetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container_detail);

            if (detail == null || !detail.getShownBusinessName().equals(business.name())) {
                detail = DetailFragment.newInstance(business, index);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_detail, detail, DetailFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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

    public void loadDetails(ArrayList<Business> businesses) {
        if (mTwoPane) {
            boolean performLoad = false;
            Business business;
            int index = 0;

            DetailFragment detail = (DetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container_detail);

            if (detail == null) {
                performLoad = true;
            }
            else {
                performLoad = true;
                String shownBusiness = detail.getShownBusinessName();
                for (Business b : businesses) {
                    if (b.name().equals(shownBusiness)) {
                        performLoad = false;
                        break;
                    }
                }
            }

            if (performLoad) {
                business = businesses.get(index);
                detail = DetailFragment.newInstance(business, index);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_detail, detail, DetailFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitAllowingStateLoss();
            }
        }
    }

    public void doDeleteBookmark() {
        VendorsFragment vendors = (VendorsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container_vendor);
        vendors.restartLoader();

        getSupportFragmentManager().popBackStackImmediate(DetailFragment.TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}