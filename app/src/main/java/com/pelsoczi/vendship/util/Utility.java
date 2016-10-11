package com.pelsoczi.vendship.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pelsoczi.vendship.R;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

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
    // YelpApi param for radius in meters, result of Math.round( miles * 1609.34 )
    private static final int[] INTERVAL_MILE_CONVERSION = {0, 1609, 8047, 12070, 24140, 40000};

    public static String getPreferredRadiusLabel(Context context, int progress) {
        if (progress == 0) {
            return context.getString(R.string.label_distance_default);
        }

        String units = getPreferredMeasureUnits(context);
        if (units.equals(context.getString(R.string.pref_distance_kilometer))) {
            String label = context.getString(R.string.pref_distance_kilometer_acronym);
            return String.valueOf(INTERVAL_KILOMETER[progress] + " " + label);
        }
        else {
            String label = context.getString(R.string.pref_distance_miles_acronym);
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
            return String.valueOf(INTERVAL_MILE_CONVERSION[progress]);
        }
    }

    public static int getProgressForRadius(Context context, String radius) {
        if (radius.equals(getPreferredConversion(context, 1))) {
            return 1;
        }
        else if (radius.equals(getPreferredConversion(context, 2))) {
            return 2;
        }
        else if (radius.equals(getPreferredConversion(context, 3))) {
            return 3;
        }
        else if (radius.equals(getPreferredConversion(context, 4))) {
            return 4;
        }
        else if (radius.equals(getPreferredConversion(context, 5))) {
            return 5;
        }
        return 0;
    }

    public static String getYelpLargeImageUrl(Business business) {
        String S = "/s.jpg";
        String M = "/m.jpg";
        String MS = "/ms.jpg";
        String O = "/o.jpg";
        String URI = business.imageUrl();

        if (URI.contains(MS)) {
            URI = URI.replace(MS, O);
        }
        else if (URI.contains(S)) {
            URI = URI.replace(S, O);
        }
        else if (URI.contains(M)) {
            URI = URI.subSequence(URI.length()-M.length(), URI.length()).toString();
        }

        return URI;
    }

    public static String getYelpAddressString(Business business) {
        if (business.location().displayAddress() != null) {
            String address = "";
            ArrayList<String> lines = business.location().displayAddress();
            for (int i = 0; i < lines.size(); i++) {
                address += lines.get(i);
                address += i < lines.size()-1 ? "\n" : "" ;
            }
            return address;
        }
        return null;
    }

    public static String getYelpCategories(Business business) {
        String categories = "";
        if (business.categories() != null) {
            for (int i = 0; i < business.categories().size(); i++) {
                categories += business.categories().get(i).name();
                categories += i < business.categories().size()-1 ? ", " : "";
            }
        }
        return categories;
    }
}