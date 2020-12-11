package com.example.robolectrictestframework.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.robolectrictestframework.R;

public class DemoActivity extends AppCompatActivity implements DemoContractor.View {

    DemoContractor.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        presenter = new DemoPresenter();
    }

    @Override
    public void showData(String resultString) {

    }
}