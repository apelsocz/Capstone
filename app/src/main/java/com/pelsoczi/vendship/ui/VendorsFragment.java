package com.pelsoczi.vendship.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pelsoczi.vendship.BuildConfig;
import com.pelsoczi.vendship.R;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = VendorsFragment.class.getSimpleName();
    private static final String LOG_TAG = VendorsFragment.class.getSimpleName();

    private static final int LOADER_VENDOR = 0;

    private Bundle mSavedInstanceState;
    private RecyclerView mRecycler;

    private FloatingActionButton mFAB;

    public VendorsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendors, container, false);
//        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_vendors);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mSavedInstanceState = (savedInstanceState != null) ? savedInstanceState : null;
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Update the Activity's title */
//        ((VendorActivity)getActivity()).updateUI();

        getLoaderManager().restartLoader(LOADER_VENDOR, null, this);

//        mFAB = (FloatingActionButton)getActivity().findViewById(R.id.floating_action_btn);
//        mFAB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                yelp();
//            }
//        });
    }

    private void yelp() {
        YelpAPIFactory apiFactory = new YelpAPIFactory(
                BuildConfig.YELP_CONSUMER_KEY,
                BuildConfig.YELP_CONSUMER_SECRET,
                BuildConfig.YELP_TOKEN,
                BuildConfig.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();

        params.put("term", "food");
        params.put("limit", "3");

        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                ArrayList<Business> businesses = searchResponse.businesses();

                for (Business busines : businesses) {
                    Log.i(LOG_TAG, busines.toString());
                }

            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                //// TODO: 16-09-21 handle http error
                Log.i(LOG_TAG, "onFailure");
                t.printStackTrace();
            }
        };
        call.enqueue(callback);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
    }
}