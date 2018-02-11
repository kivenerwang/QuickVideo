package cn.ittiger.video.app;

import android.app.Application;

import com.quickplayer.CompatApplication;

/**
 * Created by kiven on 2/11/18.
 */

public class PlayerApplication extends CompatApplication {
    public static void onCreate(Application application) {
        sApplication = application;
    }

}
