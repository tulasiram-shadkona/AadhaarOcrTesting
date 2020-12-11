package com.example.robolectrictestframework;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.preference.PowerPreference;

public class TestingAadhaarOcrApp extends Application {

    private static String TAG = "GramSevak";

    private static TestingAadhaarOcrApp instance = new TestingAadhaarOcrApp();
    public TestingAadhaarOcrApp() {
        super();
        instance = this;

    }

    /**
     * @return The singleton instance
     */
    public static TestingAadhaarOcrApp getApp() {
        return instance;
    }

    public static Context getAppContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            sDriverManager = DriverManager.getInstance();
            PowerPreference.init(this);
            FirebaseApp.initializeApp(this).getApplicationContext();
        }catch (Exception e){

        }
    }
}
