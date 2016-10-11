package com.pelsoczi.vendship;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MyApplication extends Application {

    private static final String LOG_TAG = MyApplication.class.getSimpleName();

    /** Reference to the global application as a singleton */
    private static MyApplication sInstance;

    private Tracker mTracker;

    /**
     * @return A reference to the global {@link Application}
     */
    public static MyApplication getInstance() {
        return sInstance;
    }

    /**
     *  Called when the {@link Application} is starting,
     *  before any other objects have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // Initialize components
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        mTracker = analytics.newTracker(R.xml.global_tracker);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
