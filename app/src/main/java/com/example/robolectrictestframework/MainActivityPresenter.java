package com.example.robolectrictestframework;

import android.app.Activity;

public class MainActivityPresenter implements MainActivityContractor.presenter {
    MainActivityContractor.View viewMain;
    private Activity activity;

    public MainActivityPresenter( Activity activity) {
        this.activity = activity;
    }



    @Override
    public void getGeoCooordinates(String lat, String lng) {
        viewMain.showGeoCoOrdinated(lat,lng);
    }
}
