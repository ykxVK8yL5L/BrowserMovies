package com.github.browsermovies.libs;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import java.io.File;

public class MainApplication extends Application {
    private static File mRootFile;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mRootFile = getFilesDir();
        sContext=   getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return sContext;
    }

    public static File getRootFile() {
        return mRootFile;
    }
}
