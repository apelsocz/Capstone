package com.pelsoczi.vendship.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pelsoczi.vendship.R;
import com.pelsoczi.vendship.util.Utility;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final String VENDOR_INDEX = "index";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private Business business;
    private ImageView image;
    private TextView name;
    private ImageView ratingImg;
    private TextView rating;
    private TextView category;
    private ImageView phoneImg;
    private TextView phone;
    private ImageView addressImg;
    private TextView address;
    private ImageView snippetImg;
    private TextView snippet;

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
        setHasOptionsMenu(true);
        business = (Business) getArguments().getSerializable(Business.class.getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        image = (ImageView) rootView.findViewById(R.id.vendor_image);
        name = (TextView) rootView.findViewById(R.id.vendor_name);
        ratingImg = (ImageView) rootView.findViewById(R.id.vendor_rating_image);
        rating = (TextView) rootView.findViewById(R.id.vendor_rating);
        category = (TextView) rootView.findViewById(R.id.vendor_category);
        phoneImg = (ImageView) rootView.findViewById(R.id.vendor_phone_icon);
        phone = (TextView) rootView.findViewById(R.id.vendor_phone);
        addressImg = (ImageView) rootView.findViewById(R.id.vendor_address_icon);
        address = (TextView) rootView.findViewById(R.id.vendor_address);
        snippetImg = (ImageView) rootView.findViewById(R.id.vendor_snippet_image);
        snippet = (TextView) rootView.findViewById(R.id.vendor_snippet);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (business.imageUrl() != null) {
            Picasso.with(getActivity())
                    .load(Utility.getYelpLargeImageUrl(business))
                    .fit()
                    .centerCrop()
                    .into(image);
        }
        else {
            image.setVisibility(View.GONE);
        }

        if (business.ratingImgUrlLarge() != null) {
            Picasso.with(getActivity())
                    .load(business.ratingImgUrlLarge())
                    .into(ratingImg);
        }
        else {
            ratingImg.setVisibility(View.GONE);
        }

        name.setText(business.name());

        rating.setText(String.valueOf(business.reviewCount()));

        String categories = "";
        if (business.categories() != null) {
            for (int i = 0; i < business.categories().size(); i++) {
                categories += business.categories().get(i).name();
                categories += i < business.categories().size()-1 ? ", " : "";
            }
            category.setText(categories);
        }
        else {
            category.setVisibility(View.GONE);
        }

        if (business.displayPhone() != null) {
            phone.setText(business.displayPhone());
            View.OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (business.phone() != null) {
                        createPhoneIntent();
                    }
                }
            };
            phone.setOnClickListener(click);
            phoneImg.setOnClickListener(click);
        }
        else {
            phone.setVisibility(View.GONE);
            phoneImg.setVisibility(View.GONE);
        }

        String addressString = Utility.getYelpAddressString(business);
        if (addressString != null) {
            address.setText(addressString);
            View.OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (business.location().coordinate() != null) {
                        createMapIntent();
                    }
                }
            };
            address.setOnClickListener(click);
            addressImg.setOnClickListener(click);
        }
        else {
            address.setVisibility(View.GONE);
            addressImg.setVisibility(View.GONE);
        }

        if (business.snippetText() != null) {
            snippet.setText(business.snippetText());
            String DEFAULT_URI = "/default_avatars/";
            if (!business.snippetImageUrl().contains(DEFAULT_URI)) {
                Picasso.with(getActivity())
                        .load(business.snippetImageUrl())
                        .into(snippetImg);
            }
        }
        else {
            snippet.setVisibility(View.GONE);
            snippetImg.setVisibility(View.GONE);
        }
    }

    private void createPhoneIntent() {
        String tel = "tel:";

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(tel + business.phone()));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Log.d(LOG_TAG, "Couldn't call " + business.phone() + ", no receiving apps installed");
        }
    }

    private void createMapIntent() {
        String geo = "geo:";
        String lat = String.valueOf(business.location().coordinate().latitude());
        String lon = String.valueOf(business.location().coordinate().longitude());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String label = "(" + Uri.encode(business.name()) + ")";
        intent.setData(Uri.parse(geo + lat + "," + lon + "?q=" + lat +"," + lon + label));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Log.d(LOG_TAG, "Couldn't map " + business.location().coordinate().toString()
                    + ", no receiving apps installed");
        }
    }

    private void createAttributionIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(business.mobileUrl()));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Log.d(LOG_TAG, "Couldn't browse " + business.mobileUrl()
                    + ", no receiving apps installed");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_yelp) {
            createAttributionIntent();
        }
        return super.onOptionsItemSelected(item);
    }
}