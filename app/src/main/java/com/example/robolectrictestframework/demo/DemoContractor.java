package com.example.robolectrictestframework.demo;

import android.graphics.Bitmap;

import java.util.HashMap;

public interface DemoContractor {

    interface View {
        void showData(String resultString);

    }

    interface Presenter{
        void getData(String string);
    }
    interface Interactor{
        void fetchData();
    }

}
