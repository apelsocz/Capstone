package com.pelsoczi.vendship;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pelsoczi.vendship.util.Utility;
import com.pelsoczi.vendship.util.Yelp;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = SearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public static class SearchFragment extends Fragment {

        public static final String TAG = SearchFragment.class.getSimpleName();

        private EditText mKeyword;
        private EditText mLocation;
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

            mLocation.setText(Utility.getPreferredLocation(getActivity()));

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

            //// TODO: 16-09-23 savedInstanceState
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
            Log.i(TAG, String.valueOf(enabled));
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
                    //// TODO: 16-09-27 remove decimal places
                    json.put(Yelp.PARAM_RADIUS, Utility.getPreferredConversion(getActivity(), progress));
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
    }
}