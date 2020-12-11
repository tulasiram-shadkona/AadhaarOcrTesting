package com.example.robolectrictestframework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robolectrictestframework.geoloc.MapsActivity;
import com.example.robolectrictestframework.model.OcrDataRecord;
import com.example.robolectrictestframework.textreg.DetectAadhaarActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import static com.example.robolectrictestframework.OcrDataSharedPreference.Key.MOBILE_NUM_KEY;
import static com.example.robolectrictestframework.OcrDataSharedPreference.Key.NAME_KEY;

public class MainActivity extends AppCompatActivity implements MainActivityContractor.View {

    Button ocrDetectButton, locationBtn;
    TextView textResult;
    final int CAMERA_CAPTURE = 1;
    public static final int REQUEST_IMAGE = 100;
    final int CROP_PIC = 919;
    private Uri picUri;
    private TextView latitudeResult,longitudeResult;
    private DatabaseReference mDatabase;
    private DatabaseReference   ocrRecord;
    private TextInputEditText userName, mobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ocrDetectButton = findViewById(R.id.showToast);
        textResult = findViewById(R.id.scannResult);
        locationBtn = findViewById(R.id.location_activity);
        latitudeResult = findViewById(R.id.latitudeResult);
        longitudeResult = findViewById(R.id.longitudeResult);
        userName = findViewById(R.id.editTextTextPersonName);
        mobileNumber = findViewById(R.id.editTextPhone);




        ocrDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OcrDataSharedPreference.getInstance().put(NAME_KEY, userName.getText().toString().trim());
                OcrDataSharedPreference.getInstance().put(MOBILE_NUM_KEY, mobileNumber.getText().toString().trim());
                Intent i = new Intent(MainActivity.this, DetectAadhaarActivity.class);
                startActivity(i);
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapsIntent);
            }
        });
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String str1 = extras.getString("LAT");
            String str2 = extras.getString("LNG");
            latitudeResult.setText(str1);
            longitudeResult.setText(str2);
        }

    }




    private void showImagePickerOptions() {
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


    private void launchCameraIntent() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
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
                        extractText(bitmap);
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

    private void extractText(Bitmap photo){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(MainActivity.this).build();
        Frame imageFrame = new Frame.Builder()
                .setBitmap(photo)
                .build();
        String imageText = "";
        StringBuilder stringBuilder = new StringBuilder();
        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(imageFrame);
        for (int i = 0 ; i < textBlockSparseArray.size(); i++){
            TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
            imageText = textBlock.getValue();
            stringBuilder.append("#"+imageText+"#");
            stringBuilder.append("\n");
            Log.e("Text", imageText);

        }
        textResult.setText(stringBuilder.toString());
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("AadhaarCard",textResult.getText().toString());
        if(clipboardManager == null || clipData == null) return;
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(MainActivity.this,"Label copied",Toast.LENGTH_SHORT).show();
    }

    /**
     * this function does the crop operation.
     */
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void showGeoCoOrdinated(String lat, String lng) {
       // Toast.makeText(MainActivity.this,"fdffds", Toast.LENGTH_SHORT).show();
    }
}