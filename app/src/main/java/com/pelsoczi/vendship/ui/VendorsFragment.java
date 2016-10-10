package com.pelsoczi.vendship.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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
import com.pelsoczi.vendship.data.VendorContract.VendorEntry;
import com.pelsoczi.vendship.util.Yelp;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;
import com.yelp.clientlib.entities.Coordinate;
import com.yelp.clientlib.entities.Location;
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
        mSavedInstanceState = (savedInstanceState != null) ? savedInstanceState : null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVendors = null;

        if (mSavedInstanceState == null) {
            getLoaderManager().restartLoader(LOADER_VENDOR, null, this);
        }
        else {
            if (mSavedInstanceState.containsKey(STATE_QUERY)) {
                mQueryJsonString = mSavedInstanceState.getString(STATE_QUERY);
                updateRecyclerView( !mSavedInstanceState.containsKey(STATE_VENDORS) ? null :
                        (ArrayList<Business>) mSavedInstanceState.getSerializable(STATE_VENDORS));
            }
            else {
                getLoaderManager().restartLoader(LOADER_VENDOR, null, this);
            }
        }

        mFAB = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_btn);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VendorActivity)getActivity()).showSearch(mQueryJsonString);
            }
        });
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
                    ArrayList<Business> list = searchResponse.businesses();
                    updateRecyclerView(list);
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    //// TODO: 16-09-21 handle http error
                    Log.i(LOG_TAG, "onFailure");
                    t.printStackTrace();
                }
            };
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doDataUpdated() {
        getLoaderManager().restartLoader(LOADER_VENDOR, null, this);
    }

    private void updateRecyclerView(ArrayList<Business> list) {
        initRecyclerView(list);
        if (mSavedInstanceState != null) {
            mRecycler.getLayoutManager()
                    .onRestoreInstanceState(mSavedInstanceState.getParcelable(STATE_LAYOUT));
        }
        updateDetails();
    }

    private void initRecyclerView(ArrayList<Business> list) {
        mVendors = list;
        mAdapter = new Adapter(getActivity(), list);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(null);
        mRecycler.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void updateDetails() {
        if (mVendors != null) {
            if (mVendors.size() > 0) {
                ((VendorActivity)getActivity()).loadDetails(mVendors.get(0), 0);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_VENDOR:
                return new CursorLoader(
                        getActivity(),
                        VendorEntry.CONTENT_URI,
                        VendorEntry.PROJECTION_COLUMNS,
                        null,
                        null,
                        VendorEntry._ID + " ASC"
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Business> list = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            int yelpId = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_YELP_ID);
            int name = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_NAME);
            int image = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_IMAGE);
            int rating = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_RATING);
            int ratingImg = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_RATING_IMG);
            int reviewCount = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_REVIEW_COUNT);
            int category = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_CATEGORIES);
            int phone = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_PHONE);
            int phoneDisplay = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_PHONE_DISPLAY);
            int address = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_ADDRESS);
            int longitude = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_LONGITUDE);
            int latitude = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_LATITUDE);
            int snippet = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_SNIPPET);
            int snippetImg = data.getColumnIndex(VendorEntry.COLUMN_VENDOR_SNIPPET_IMG);
            do {
                Business.Builder builder = Business.builder();

                builder.id(data.getString(yelpId))
                        .name(data.getString(name))
                        .imageUrl(data.getString(image))
                        .rating(data.getDouble(rating))
                        .ratingImgUrlLarge(data.getString(ratingImg))
                        .reviewCount(data.getInt(reviewCount));

                if (!data.getString(phone).equals("")) {
                    builder = builder
                            .phone(data.getString(phone))
                            .displayPhone(data.getString(phoneDisplay));
                }

                if (!data.getString(category).equals("")) {
                    String value = data.getString(category);
                    ArrayList<Category> categoryList = new ArrayList<Category>();
                    for (String string : value.split(",")) {
                        categoryList.add(Category.builder()
                                .name(string)
                                .alias("")
                                .build());
                    }
                    builder = builder.categories(categoryList);
                }

                if (!data.getString(address).equals("")) {
                    ArrayList<String> addressList = new ArrayList<String>();
                    addressList.add(data.getString(address));
                    Coordinate coord = Coordinate.builder()
                            .longitude(data.getDouble(longitude))
                            .latitude(data.getDouble(latitude))
                            .build();
                    Location location = Location.builder()
                            .displayAddress(addressList)
                            .coordinate(coord)
                            .build();
                    builder = builder.location(location);
                }

                if (!data.getString(snippet).equals("")) {
                    builder = builder
                            .snippetText(data.getString(snippet))
                            .snippetImageUrl(data.getString(snippetImg));
                }

                Business business = builder.build();
                list.add(business);
            } while (data.moveToNext());
        }
        updateRecyclerView(list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
    }
}