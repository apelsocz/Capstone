package com.pelsoczi.vendship;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.pelsoczi.vendship.ui.DetailFragment;
import com.yelp.clientlib.entities.Business;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        Business business = (Business) intent.getExtras()
                .getSerializable(Business.class.getSimpleName());
        int index = intent.getExtras().getInt(DetailFragment.VENDOR_INDEX);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail, DetailFragment.newInstance(business, index),
                            DetailFragment.TAG)
                    .commit();
        }
    }
}