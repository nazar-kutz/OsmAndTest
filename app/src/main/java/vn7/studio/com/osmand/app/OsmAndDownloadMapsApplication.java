package vn7.studio.com.osmand.app;

import android.app.Application;
import android.content.Context;

public class OsmAndDownloadMapsApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
