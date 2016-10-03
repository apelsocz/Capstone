package com.pelsoczi.vendship.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pelsoczi.vendship.BuildConfig;
import com.pelsoczi.vendship.R;
import com.pelsoczi.vendship.VendorActivity;
import com.pelsoczi.vendship.util.Yelp;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = VendorsFragment.class.getSimpleName();
    private static final String LOG_TAG = VendorsFragment.class.getSimpleName();

    private static final String STATE_LAYOUT = "layoutState";
    private static final String STATE_QUERY = "queryState";
    private static final String STATE_OFFSET = "offsetState";
    private static final String STATE_VENDORS = "vendorState";

    private static final int LOADER_VENDOR = 0;

    private Bundle mSavedInstanceState;
    private FloatingActionButton mFAB;
    private RecyclerView mRecycler;
    private Adapter mAdapter;
    private ArrayList<Business> mVendors;
    private String mQueryJsonString;

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
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_VENDORS)) {
                mVendors = (ArrayList<Business>) savedInstanceState.getSerializable(STATE_VENDORS);
                updateRecyclerView();
            }
            if (savedInstanceState.containsKey(STATE_QUERY)) {
                mQueryJsonString = savedInstanceState.getString(STATE_QUERY);
            }
            mSavedInstanceState = savedInstanceState;
        }

        mFAB = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_btn);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VendorActivity)getActivity()).doSearchActivity(mQueryJsonString);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /** Update the Activity's title */
//        ((VendorActivity)getActivity()).updateUI();

//        getLoaderManager().restartLoader(LOADER_VENDOR, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mVendors != null && !mVendors.isEmpty()) {
            outState.putSerializable(STATE_VENDORS, mVendors);
        }
        if (mQueryJsonString != null && !mQueryJsonString.equals("")) {
            outState.putString(STATE_QUERY, mQueryJsonString);
        }
        if (mRecycler.getLayoutManager() != null) {
            outState.putParcelable(STATE_LAYOUT, mRecycler.getLayoutManager().onSaveInstanceState());
        }

        mSavedInstanceState = outState;
    }

    public void searchYelp(String jsonAsString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonAsString);
            mQueryJsonString = jsonAsString;

            Map<String, String> params = new HashMap<>();

            params.put(Yelp.PARAM_TERM, jsonObject.optString(Yelp.PARAM_TERM));

            String location = jsonObject.optString(Yelp.PARAM_LOCATION);

            if (jsonObject.has(Yelp.PARAM_SORT_BY)) {
                params.put(Yelp.PARAM_SORT_BY, jsonObject.optString(Yelp.PARAM_SORT_BY));
            }

            if (jsonObject.has(Yelp.PARAM_RADIUS)) {
                params.put(Yelp.PARAM_RADIUS, jsonObject.optString(Yelp.PARAM_RADIUS));
            }

            YelpAPIFactory apiFactory = new YelpAPIFactory(
                    BuildConfig.YELP_CONSUMER_KEY,
                    BuildConfig.YELP_CONSUMER_SECRET,
                    BuildConfig.YELP_TOKEN,
                    BuildConfig.YELP_TOKEN_SECRET);

            YelpAPI yelpAPI = apiFactory.createAPI();
            Call<SearchResponse> call = yelpAPI.search(location, params);

            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    SearchResponse searchResponse = response.body();
                    mVendors = searchResponse.businesses();
                    updateRecyclerView();
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    //// TODO: 16-09-21 handle http error
                    Log.i(TAG, "onFailure");
                    t.printStackTrace();
                }
            };
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateRecyclerView() {
        initRecyclerView();
        if (mSavedInstanceState != null) {
            mRecycler.getLayoutManager()
                    .onRestoreInstanceState(mSavedInstanceState.getParcelable(STATE_LAYOUT));
        }
//        updateDetails();
    }

    private void initRecyclerView() {
        mAdapter = new Adapter(getActivity(), mVendors);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(null);
        mRecycler.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
    }
}