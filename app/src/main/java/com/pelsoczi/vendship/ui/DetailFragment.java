package com.pelsoczi.vendship.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pelsoczi.vendship.R;
import com.pelsoczi.vendship.data.VendorContract.VendorEntry;
import com.pelsoczi.vendship.util.Utility;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final String VENDOR_INDEX = "index";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private Business business;
    private boolean mIsFavorite;

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

        Cursor cursor = getActivity().getContentResolver().query(
                VendorEntry.CONTENT_URI,
                VendorEntry.PROJECTION_COLUMNS,
                VendorEntry.COLUMN_VENDOR_YELP_ID + " = ?",
                new String[] {String.valueOf(business.id())},
                null
        );

        mIsFavorite = cursor!= null && cursor.moveToFirst();
        if (mIsFavorite) {
            cursor.close();
        }

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

        String categories = Utility.getYelpCategories(business);
        if (categories != null) {
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
        int iconRes = mIsFavorite ?
                R.drawable.ic_bookmark_black_24dp : R.drawable.ic_bookmark_border_black_24dp;
        menu.findItem(R.id.action_bookmark).setIcon(iconRes);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_yelp) {
            createAttributionIntent();
            return true;
        }
        else if (item.getItemId() == R.id.action_bookmark) {
            if (!mIsFavorite) {
                new SaveVendorTask().execute(business);
                return true;
            }
            else {
                new DeleteVendorTask().execute(business);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveVendorTask extends AsyncTask<Business, Void, Void> {
        @Override
        protected Void doInBackground(Business... params) {
            Business business = params[0];

            ContentValues values = new ContentValues();
            values.put(VendorEntry.COLUMN_VENDOR_YELP_ID, business.id());
            values.put(VendorEntry.COLUMN_VENDOR_NAME, business.name());
            values.put(VendorEntry.COLUMN_VENDOR_IMAGE, Utility.getYelpLargeImageUrl(business));
            values.put(VendorEntry.COLUMN_VENDOR_RATING, business.rating());
            values.put(VendorEntry.COLUMN_VENDOR_RATING_IMG, business.ratingImgUrlLarge());
            values.put(VendorEntry.COLUMN_VENDOR_REVIEW_COUNT, business.reviewCount());
            values.put(VendorEntry.COLUMN_VENDOR_CATEGORIES, Utility.getYelpCategories(business));
            boolean phone = business.displayPhone() != null;
            values.put(VendorEntry.COLUMN_VENDOR_PHONE, phone ? business.phone() : "");
            values.put(VendorEntry.COLUMN_VENDOR_PHONE_DISPLAY, phone ? business.displayPhone() : "");
            boolean address = Utility.getYelpAddressString(business) != null;
            values.put(VendorEntry.COLUMN_VENDOR_ADDRESS, address ?
                    Utility.getYelpAddressString(business) : "");
            values.put(VendorEntry.COLUMN_VENDOR_LONGITUDE, address ?
                    String.valueOf(business.location().coordinate().longitude()) : "");
            values.put(VendorEntry.COLUMN_VENDOR_LATITUDE, address ?
                    String.valueOf(business.location().coordinate().latitude()) : "");
            values.put(VendorEntry.COLUMN_VENDOR_SNIPPET, business.snippetText()!= null ?
                    business.snippetText() : "");
            values.put(VendorEntry.COLUMN_VENDOR_SNIPPET_IMG, business.snippetImageUrl());

            getActivity().getContentResolver().insert(VendorEntry.CONTENT_URI, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsFavorite = !mIsFavorite;
            Toast.makeText(getContext(), "INSERT", Toast.LENGTH_SHORT).show();
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    private class DeleteVendorTask extends AsyncTask<Business, Void, Void> {
        @Override
        protected Void doInBackground(Business... params) {
            String selection = VendorEntry.COLUMN_VENDOR_YELP_ID + " = ?";
            String[] selectionArgs = new String[] {String.valueOf(business.id())};
            getActivity().getContentResolver().delete(VendorEntry.CONTENT_URI, selection,
                    selectionArgs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsFavorite = !mIsFavorite;
            getActivity().supportInvalidateOptionsMenu();
        }
    }
}