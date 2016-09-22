package com.pelsoczi.vendship.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorsFragment extends Fragment {

    public static final String TAG = VendorsFragment.class.getSimpleName();
    private static final String LOG_TAG = VendorsFragment.class.getSimpleName();

    private Bundle mSavedInstanceState;
    private RecyclerView mRecycler;

    private FloatingActionButton mFAB;

    public VendorsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendors, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_vendors);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // savedInstanceState - retrieve
        mSavedInstanceState = (savedInstanceState != null) ? savedInstanceState : null;
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Update the Activity's title */
//        ((VendorActivity)getActivity()).updateUI();

//        downloadVendors();

        mFAB = (FloatingActionButton)getActivity().findViewById(R.id.floating_action_btn);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yelp();
//                new yelpTask().execute();
            }
        });
    }

    private void yelp() {
        YelpAPIFactory apiFactory = new YelpAPIFactory(
                BuildConfig.YELP_CONSUMER_KEY,
                BuildConfig.YELP_CONSUMER_SECRET,
                BuildConfig.YELP_TOKEN,
                BuildConfig.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();

// general params
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


//        try {
//            Response<SearchResponse> response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    private class yelpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            YelpAPIFactory apiFactory = new YelpAPIFactory(
                    BuildConfig.YELP_CONSUMER_KEY,
                    BuildConfig.YELP_CONSUMER_SECRET,
                    BuildConfig.YELP_TOKEN,
                    BuildConfig.YELP_TOKEN_SECRET);
            YelpAPI yelpAPI = apiFactory.createAPI();

            Map<String, String> param = new HashMap<>();

// general params
            param.put("term", "food");
//            param.put("limit", "3");

            Call<SearchResponse> call = yelpAPI.search("Dallas", param);

            try {
                Response<SearchResponse> response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}