package com.example.robolectrictestframework.textreg;

import android.graphics.Bitmap;

import java.util.HashMap;

public interface DetectAadhaarContract {
    interface View {
        void showAadhaarDetectOptions();

        void showCameraOptions();

        void showAadharInfo(HashMap<String, String> map);
    }

    interface Presenter {
        void getImageDataAsText(Bitmap bitmap);
    }
}
