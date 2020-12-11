package com.example.robolectrictestframework;

public interface MainActivityContractor {

    interface presenter{
       void getGeoCooordinates(String lat, String lng);
    }

    interface View{
        void showGeoCoOrdinated(String lat, String lng);
    }

}
