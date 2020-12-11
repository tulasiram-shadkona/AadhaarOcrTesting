package com.example.robolectrictestframework.textreg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robolectrictestframework.ImagePickerActivity;
import com.example.robolectrictestframework.MainActivity;
import com.example.robolectrictestframework.OcrDataSharedPreference;
import com.example.robolectrictestframework.R;
import com.example.robolectrictestframework.model.OcrDataRecord;
import com.example.robolectrictestframework.utils.Tools;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shadkona.aadhaar.ocr.mylibrary.DetectAadhaarContract;
import com.shadkona.aadhaar.ocr.mylibrary.DetectAadhaarPresenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.robolectrictestframework.OcrDataSharedPreference.Key.IMAGE_TEXT;
import static com.example.robolectrictestframework.OcrDataSharedPreference.Key.MOBILE_NUM_KEY;
import static com.example.robolectrictestframework.OcrDataSharedPreference.Key.NAME_KEY;

public class DetectAadhaarActivity extends AppCompatActivity implements DetectAadhaarContract.View , View.OnClickListener {

    //DetectAadhaarContract.View
    private DetectAadhaarContract.Presenter presenter;

    private Button aadharDetectBtn;
    private TextView resultTextView;
    public static final int REQUEST_IMAGE = 100;
    OcrDataSharedPreference pref;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_aadhaar);
         pref = OcrDataSharedPreference.getInstance();
        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this).getApplicationContext();
        initViews();
        presenter = new DetectAadhaarPresenter(this,DetectAadhaarActivity.this);
    }

    private void initViews() {
        aadharDetectBtn = (Button)findViewById(R.id.camera_action);
        resultTextView = (TextView)findViewById(R.id.aadhaarResult);
        aadharDetectBtn.setOnClickListener(this);
    }

//    @Override
//    public void showAadhaardata(String resultString) {
//        resultTextView.setText(resultString);
//        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clipData = ClipData.newPlainText("AadhaarCard",resultTextView.getText().toString());
//        if(clipboardManager == null || clipData == null) return;
//        clipboardManager.setPrimaryClip(clipData);
//        Toast.makeText(DetectAadhaarActivity.this,"Data copied",Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void showAadharInfo(HashMap<String, String> map) {
        TextView aadhaarNumber = (TextView) findViewById(R.id.aadhaarNumber);
        TextView aadhaarName = (TextView) findViewById(R.id.aadhaarName);
        TextView aadhaarDateOfYear = (TextView) findViewById(R.id.aadharDateOfYear);
        TextView aadhaarGender = (TextView) findViewById(R.id.aadharGender);

        pref.put(IMAGE_TEXT,map.get("IMAGE_TEXT"));
        resultTextView.setText(map.get("IMAGE_TEXT"));
        aadhaarNumber.setText(map.get("AADHAR"));
        aadhaarName.setText(map.get("NAME"));
        aadhaarDateOfYear.setText(map.get("DATE_OF_YEAR"));
        aadhaarGender.setText(map.get("GENDER"));
        try {
            setToFireBaseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setToFireBaseDB() throws Exception {
        String usrNameStr =  pref.getString(NAME_KEY);
        String mobileNum = pref.getString(MOBILE_NUM_KEY);
        String ocrImgText = pref.getString(IMAGE_TEXT);
        String uuid = Tools.generateUuid();
        writeNewUser(uuid,usrNameStr,mobileNum,ocrImgText);
    }


    @Override
    public void showAadhaarDetectOptions() {
        Dexter.withActivity(DetectAadhaarActivity.this   )
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            showCameraOptions();
                        } else {
                            // TODO - handle permission denied case
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void showCameraOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    @Override
    public void onClick(View view) {
        showAadhaarDetectOptions();
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(DetectAadhaarActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(DetectAadhaarActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        try {
            if (requestCode == REQUEST_IMAGE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getParcelableExtra("path");
                    try {
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                        extractText(bitmap);
                        presenter.getImageDataAsText(bitmap);
                        // loading profile image from local cache
//                        loadProfile(uri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /*if (resultCode == Activity.RESULT_OK) {
                if(requestCode == 999 ){
                    Bitmap photo = (Bitmap) intent.getExtras().get("data");
                    extractText(photo);
                    // get the Uri for the captured image
//                    picUri = intent.getData();
//                    performCrop();
                }else if(requestCode == CROP_PIC){

                    // get the returned data
                    Bundle extras = intent.getExtras();
                    // get the cropped bitmap
                    Bitmap thePic = extras.getParcelable("data");
                    extractText(thePic);

                }

            }*/


        } catch (Exception e) {
        }

    }

    private void writeNewUser(String userId, String name, String mobile, String ocrImgText) {
        OcrDataRecord user = new OcrDataRecord(name, mobile,ocrImgText);

        mDatabase.child("users").child(userId).setValue(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG_TAG", e.getLocalizedMessage());
            }
        });
    }



}