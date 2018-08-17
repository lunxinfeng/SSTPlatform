package com.fintech.sst;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    private static App application;

    public static App getApplication() {
        return application;
    }

    public static Context getAppContext() {
        return application.getApplicationContext();
    }

    public App() {
        this.application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
