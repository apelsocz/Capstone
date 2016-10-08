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

        //// TODO: 2016-10-06  psf String COLUMN = "_name"


        public static final String[] PROJECTION_COLUMNS = {
                _ID,
        };

        public static Uri buildVendorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}