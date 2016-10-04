package com.pelsoczi.vendship.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pelsoczi.vendship.R;
import com.yelp.clientlib.entities.Business;

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final String VENDOR_INDEX = "index";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private Business business;

    public static DetailFragment newInstance(Business business, int index) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Business.class.getSimpleName(), business);
        bundle.putInt(VENDOR_INDEX, index);
        DetailFragment detail = new DetailFragment();
        detail.setArguments(bundle);
        return detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        business = (Business) getArguments().getSerializable(Business.class.getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }
}
