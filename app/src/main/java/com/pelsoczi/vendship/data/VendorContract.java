package com.pelsoczi.vendship.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class VendorContract {

    public static final String CONTENT_AUTHORITY = "com.pelsoczi.vendship.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_VENDOR = "vendor";

    public static final class VendorEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VENDOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VENDOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VENDOR;

        public static final String TABLE_NAME = "vendor";

        public static final String COLUMN_VENDOR_YELP_ID = "yelp_id";
        public static final String COLUMN_VENDOR_NAME = "name";
        public static final String COLUMN_VENDOR_IMAGE = "image";
        public static final String COLUMN_VENDOR_RATING = "rating";
        public static final String COLUMN_VENDOR_RATING_IMG = "rating_img";
        public static final String COLUMN_VENDOR_REVIEW_COUNT = "review_count";
        public static final String COLUMN_VENDOR_CATEGORIES = "categories";
        public static final String COLUMN_VENDOR_PHONE = "phone";
        public static final String COLUMN_VENDOR_PHONE_DISPLAY = "phone_display";
        public static final String COLUMN_VENDOR_ADDRESS = "address";
        public static final String COLUMN_VENDOR_LONGITUDE = "longitude";
        public static final String COLUMN_VENDOR_LATITUDE = "latitide";
        public static final String COLUMN_VENDOR_SNIPPET = "snippet";
        public static final String COLUMN_VENDOR_SNIPPET_IMG = "snippet_img";

        public static final String[] PROJECTION_COLUMNS = {
                _ID,
                COLUMN_VENDOR_YELP_ID,
                COLUMN_VENDOR_NAME,
                COLUMN_VENDOR_IMAGE,
                COLUMN_VENDOR_RATING,
                COLUMN_VENDOR_RATING_IMG,
                COLUMN_VENDOR_REVIEW_COUNT,
                COLUMN_VENDOR_CATEGORIES,
                COLUMN_VENDOR_PHONE,
                COLUMN_VENDOR_PHONE_DISPLAY,
                COLUMN_VENDOR_ADDRESS,
                COLUMN_VENDOR_LONGITUDE,
                COLUMN_VENDOR_LATITUDE,
                COLUMN_VENDOR_SNIPPET,
                COLUMN_VENDOR_SNIPPET_IMG
        };

        public static Uri buildVendorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}