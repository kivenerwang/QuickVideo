package com.quickplayer;

import android.app.Application;

/**
 * Created by kiven on 2/11/18.
 */

public class CompatApplication {
    protected static Application sApplication = null;
    private static boolean mNeedShowWifiTip = true;

    public static boolean isNeedShowWifiTip() {
        return mNeedShowWifiTip;
    }

    public static void setNeedShowWifiTip(boolean mNeedShowWifiTip) {
        CompatApplication.mNeedShowWifiTip = mNeedShowWifiTip;
    }

    public static Application getApplication() {
        return sApplication;
    }

    protected static CompatApplication sCompatBrowser;

    public static CompatApplication getInstance() {
        return sCompatBrowser;
    }

}
