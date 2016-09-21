package com.pelsoczi.vendship;

import android.app.Application;

public class MyApplication extends Application {

    /** Reference to the global application as a singleton */
    private static MyApplication sInstance;

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
    }
}
