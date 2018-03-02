package com.quickplayer;

import android.app.Application;

/**
 * Created by kiven on 2/11/18.
 */

public class CompatApplication {
    protected static Application sApplication = null;

    public static Application getApplication() {
        return sApplication;
    }

    protected static CompatApplication sCompatBrowser;

    public static CompatApplication getInstance() {
        return sCompatBrowser;
    }

}
