package com.pelsoczi.vendship.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class VendorProvider extends ContentProvider {

    // the UriMatcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private VendorDbHelper mOpenHelper;

    static final int VENDOR = 100;
    static final int VENDOR_ID = 101;

    private static final SQLiteQueryBuilder sVendorQueryBuilder;

    static {
        sVendorQueryBuilder = new SQLiteQueryBuilder();
        // set tables here if performing a join
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = VendorContract.CONTENT_AUTHORITY;

        // create a corresponding code for each type or URI added
        matcher.addURI(authority, VendorContract.PATH_VENDOR, VENDOR);
        matcher.addURI(authority, VendorContract.PATH_VENDOR + "/#", VENDOR_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = VendorDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case VENDOR:
                return VendorContract.VendorEntry.CONTENT_TYPE;
            case VENDOR_ID:
                return VendorContract.VendorEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case VENDOR:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VendorContract.VendorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case VENDOR_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VendorContract.VendorEntry.TABLE_NAME,
                        projection,
                        VendorContract.VendorEntry._ID + " = ?",
                        new String[] { String.valueOf(_id) },
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case VENDOR: {
                _id = db.insert(VendorContract.VendorEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = VendorContract.VendorEntry.buildVendorUri(_id);
                } else {
                    throw new UnsupportedOperationException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        if (selection == null) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {
            case VENDOR: {
                rowsDeleted = db.delete(
                        VendorContract.VendorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case VENDOR_ID: {
                String _id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(VendorContract.VendorEntry.TABLE_NAME,
                            VendorContract.VendorEntry._ID + " = " + _id,
                            null);
                } else {
                    rowsDeleted = db.delete(VendorContract.VendorEntry.TABLE_NAME,
                            selection + " AND " + VendorContract.VendorEntry._ID + " = " + _id,
                            selectionArgs);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case VENDOR: {
                rowsUpdated = db.update(VendorContract.VendorEntry.TABLE_NAME, values, selection,
                        selectionArgs);
            }
            case VENDOR_ID: {
                String _id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(VendorContract.VendorEntry.TABLE_NAME,
                            values,
                            VendorContract.VendorEntry._ID + " = " + _id,
                            null);
                }
                else {
                    rowsUpdated = db.update(VendorContract.VendorEntry.TABLE_NAME,
                            values,
                            selection + " AND " + VendorContract.VendorEntry._ID + " = " + _id,
                            selectionArgs);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}