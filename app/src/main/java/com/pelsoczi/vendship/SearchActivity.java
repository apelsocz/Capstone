package com.pelsoczi.vendship;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pelsoczi.vendship.util.Utility;
import com.pelsoczi.vendship.util.Yelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    public static final String TAG = SearchActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS = 1;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    String mQueryJsonString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent launchIntent = getIntent();
        if (launchIntent != null && launchIntent.hasExtra(Yelp.KEY_YELP)) {
            mQueryJsonString = launchIntent.getStringExtra(Yelp.KEY_YELP);
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "mGoogleApiClient, onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "mGoogleApiClient, onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "mGoogleApiClient, onConnectionFailed");
    }

    private void checkPermissions() {

        boolean permitted = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permitted) {
            requestLocation();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean permitted = true;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    permitted = false;
                    break;
                }
            }
            if (permitted) {
                requestLocation();
            }
        }
    }

    private void requestLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            SearchFragment search = (SearchFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container_search);
            search.setLocationCity(addresses.get(0).getLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class SearchFragment extends Fragment {

        public static final String TAG = SearchFragment.class.getSimpleName();

        private EditText mKeyword;
        private EditText mLocation;
        private ImageView mCurrentLocation;
        private RadioGroup mSortGroup;
        private RadioButton mSortDefault;
        private RadioButton mSortDistance;
        private RadioButton mSortRating;
        private SeekBar mDistanceSeek;
        private TextView mDistanceLabel;
        private MenuItem mActionSearch;

        /* Validates keyword and location inputs, to enable/disable search functionality */
        private TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateUI();
            }
        };

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);

            mKeyword = (EditText) rootView.findViewById(R.id.search_keyword);
            mLocation = (EditText) rootView.findViewById(R.id.search_location);
            mCurrentLocation = (ImageView) rootView.findViewById(R.id.search_location_icon);
            mSortGroup = (RadioGroup) rootView.findViewById(R.id.search_sort_group);
            mSortDefault = (RadioButton) rootView.findViewById(R.id.search_sort_default);
            mSortDistance = (RadioButton) rootView.findViewById(R.id.search_sort_distance);
            mSortRating = (RadioButton) rootView.findViewById(R.id.search_sort_rating);
            mDistanceSeek = (SeekBar) rootView.findViewById(R.id.search_distance_slider);
            mDistanceLabel = (TextView) rootView.findViewById(R.id.search_distance_label);

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);

            String keyword = "";
            String location = Utility.getPreferredLocation(getActivity());
            int progress = 0;

            String queryJsonString = ((SearchActivity)getActivity()).mQueryJsonString;
            if (queryJsonString != null && !queryJsonString.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(queryJsonString);
                    if (jsonObject.has(Yelp.PARAM_TERM)) {
                        keyword = jsonObject.getString(Yelp.PARAM_TERM);
                    }
                    if (jsonObject.has(Yelp.PARAM_LOCATION)) {
                        location = jsonObject.getString(Yelp.PARAM_LOCATION);
                    }
                    if (jsonObject.has(Yelp.PARAM_SORT_BY)) {
                        if (jsonObject.getString(Yelp.PARAM_SORT_BY).equals("1")) {
                            mSortDistance.setChecked(true);
                        }
                        else if (jsonObject.getString(Yelp.PARAM_SORT_BY).equals("2")) {
                            mSortRating.setChecked(true);
                        }
                    }
                    if (jsonObject.has(Yelp.PARAM_RADIUS)) {
                        progress = Utility.getProgressForRadius(getActivity(),
                                jsonObject.getString(Yelp.PARAM_RADIUS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mKeyword.setSelectAllOnFocus(true);
            mKeyword.setText(keyword);

            mLocation.setText(location);

            mCurrentLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SearchActivity) getActivity()).mGoogleApiClient.isConnected()) {
                        ((SearchActivity)getActivity()).checkPermissions();
                    }
                }
            });

            if (!mSortDistance.isChecked() && !mSortRating.isChecked()) {
                mSortGroup.check(R.id.search_sort_default);
            }

            mDistanceSeek.setMax(Utility.MAX_PROGRESS);
            mDistanceSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mDistanceLabel.setText(Utility.getPreferredRadiusLabel(getActivity(), progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            mDistanceSeek.setProgress(progress);
        }

        @Override
        public void onResume() {
            super.onResume();

            mKeyword.addTextChangedListener(mTextWatcher);
            mLocation.addTextChangedListener(mTextWatcher);
        }

        private void validateUI() {
            boolean enabled = !(mKeyword.length() == 0 || mLocation.length() == 0);
            mActionSearch.setVisible(enabled);
        }

        @Override
        public void onStop() {
            mKeyword.removeTextChangedListener(mTextWatcher);
            mLocation.removeTextChangedListener(mTextWatcher);

            super.onStop();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.search, menu);
            mActionSearch = menu.findItem(R.id.action_search);

            validateUI();

            super.onPrepareOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_search) {
                yelp();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void yelp() {
            JSONObject json = new JSONObject();
            try {
                json.put(Yelp.PARAM_TERM, mKeyword.getText().toString());
                json.put(Yelp.PARAM_LOCATION, mLocation.getText().toString());

                if (mSortDistance.isChecked()) {
                    json.put(Yelp.PARAM_SORT_BY, "1");
                }
                else if (mSortRating.isChecked()) {
                    json.put(Yelp.PARAM_SORT_BY, "2");
                }

                int progress = mDistanceSeek.getProgress();
                if (progress != 0) {
                    json.put(Yelp.PARAM_RADIUS,
                            Utility.getPreferredConversion(getActivity(), progress));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                Intent result = new Intent();
                result.putExtra(Yelp.KEY_YELP, json.toString());
                getActivity().setResult(Activity.RESULT_OK, result);
                getActivity().finish();
            }
        }

        private void setLocationCity(String localicity) {
            if (!mLocation.getText().toString().equals(localicity)) {
                mLocation.setText(localicity);
            }
        }
    }
}