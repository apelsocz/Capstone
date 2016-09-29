package com.pelsoczi.vendship.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pelsoczi.vendship.R;

public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),"");
    }

    public static String getPreferredMeasureUnits(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String defValue = context.getString(R.string.pref_distance_default);
        String key = prefs.getString(context.getString(R.string.pref_distance_key), defValue);
        String units = key.equals(defValue) ? context.getString(R.string.pref_distance_miles) :
                context.getString(R.string.pref_distance_kilometer);
        return units;
    }

    /* Used for converting progress bar interval */
    public static final int MAX_PROGRESS = 5;
    private static final double[] INTERVAL_KILOMETER = {0, 1, 5, 10, 20, 40};
    private static final double[] INTERVAL_MILE = {0, 1, 5, 7.5, 15, 25};

    public static String getPreferredRadiusLabel(Context context, int progress) {
        if (progress == 0) {
            return "@string/no_radius";
        }

        String units = getPreferredMeasureUnits(context);
        if (units.equals(context.getString(R.string.pref_distance_kilometer))) {
            String label = "km";
            return String.valueOf(INTERVAL_KILOMETER[progress] + " " + label);
        }
        else {
            String label = "mi";
            return String.valueOf(INTERVAL_MILE[progress] + " " + label);
        }
    }

    public static String getPreferredConversion(Context context, int progress) {
        if (progress == 0) {
            return null;
        }

        String units = getPreferredMeasureUnits(context);
        if (units.equals(context.getString(R.string.pref_distance_kilometer))) {
            return String.valueOf(INTERVAL_KILOMETER[progress] * 1000);
        }
        else {
            return String.valueOf(INTERVAL_KILOMETER[progress] * 1609.34);
        }
    }
}
