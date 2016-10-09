package com.pelsoczi.vendship.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pelsoczi.vendship.data.VendorContract.VendorEntry;


public class VendorDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = VendorDbHelper.class.getSimpleName();

    private static VendorDbHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "vendor.db";
    private static final int DATABASE_VERSION = 2;

    public static synchronized VendorDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new VendorDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private VendorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_POSTS_TABLE = "CREATE TABLE " + VendorEntry.TABLE_NAME +
                " (" +
                    VendorEntry._ID + " INTEGER PRIMARY KEY," +
                    VendorEntry.COLUMN_VENDOR_YELP_ID + " TEXT NOT NULL," +
                    VendorEntry.COLUMN_VENDOR_NAME + " TEXT NOT NULL," +
                    VendorEntry.COLUMN_VENDOR_IMAGE + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_RATING + " TEXT NOT NULL," +
                    VendorEntry.COLUMN_VENDOR_RATING_IMG + " TEXT NOT NULL," +
                    VendorEntry.COLUMN_VENDOR_REVIEW_COUNT + " TEXT NOT NULL," +
                    VendorEntry.COLUMN_VENDOR_CATEGORIES + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_PHONE + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_PHONE_DISPLAY + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_ADDRESS + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_LONGITUDE + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_LATITUDE + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_SNIPPET + " TEXT," +
                    VendorEntry.COLUMN_VENDOR_SNIPPET_IMG + " TEXT," +
                    " UNIQUE (" + VendorEntry.COLUMN_VENDOR_YELP_ID + ") ON CONFLICT REPLACE" +
                ");";

        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + VendorEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}