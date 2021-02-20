package com.example.robolectrictestframework.textreg;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.example.robolectrictestframework.utils.DateUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetectAadhaarPresenter implements DetectAadhaarContract.Presenter {
    private static final String TAG = "DetectAadhaarPresent";
    HashMap<String, String> metadataMap = new HashMap<String, String>();
    private DetectAadhaarContract.View detectAadharView;
    private Activity activity;

    public DetectAadhaarPresenter(DetectAadhaarContract.View detectAadharView, Activity activity) {
        this.detectAadharView = detectAadharView;
        this.activity = activity;
    }

    @Override
    public void getImageDataAsText(Bitmap photo) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(activity).build();
        Frame imageFrame = new Frame.Builder()
                .setBitmap(photo)
                .build();
        String imageText = "";
        StringBuilder stringBuilder = new StringBuilder();
        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(imageFrame);

        for (int i = 0; i < textBlockSparseArray.size(); i++) {
            TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
            imageText = textBlock.getValue();
            Log.d("Language : ", imageText + " : " + textBlock.getLanguage());
            stringBuilder.append("#" + imageText + "#");
            stringBuilder.append("\n");

            getTextType(imageText);
        }
        //made the changes for commit
//        textResult.setText(stringBuilder.toString());
//        detectAadharView.showAadhaardata(stringBuilder.toString());
        detectAadharView.showAadharInfo(metadataMap);

    }

    public void getTextType(String val) {
        try {
            String type = "";

            if (val.contains("\n")) {
                String valArr[] = val.split("\n");

                if (valArr.length > 0) {
                    for (int newlineIdx = 0; newlineIdx < valArr.length; newlineIdx++) {
                        setMetaData(valArr[newlineIdx]);
                    }
                }
            } else {
                setMetaData(val);
            }
        } catch (Exception e) {

        }

    }

    public void setMetaData(String val) throws Exception {
        try {
            String aadharRegex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
            String nameRegex = "^[a-zA-Z\\s]*$";

            Matcher aadharMatcher = getPatternMatcher(aadharRegex, val);
            Matcher nameMatcher = getPatternMatcher(nameRegex, val);

            String metaData = "OTHER";
            String srcVal = val.toUpperCase();
            String tgtVal = val;

            if (srcVal.contains("MALE") || srcVal.contains("FEMALE") || srcVal.contains("TRANS")) {
                metaData = "GENDER";
                if (val.contains("/")) {
                    tgtVal = val.split("/")[1];
                } else {
                    tgtVal = val.split(" ")[1];
                }

            } else if (srcVal.contains("YEAR") || srcVal.contains("BIRTH") || srcVal.contains("DATE") || srcVal.contains("DOB") ||
                    srcVal.contains("YEAR OF") || srcVal.contains("YOB")) {
                metaData = "DATE_OF_YEAR";

                if (val.contains(":")) {
                    tgtVal = val.split(":")[1];
                } else {
                    String dobValArr[] = val.split(" ");
                    int dobValLen = dobValArr.length;
                    tgtVal = dobValArr[dobValLen - 1];
                }

                tgtVal = getFormatedDate(tgtVal);

            } else if (aadharMatcher.matches()) {
                metaData = "AADHAR";
            } else if (nameMatcher.matches() && !srcVal.contains("GOVERNMENT") && !srcVal.contains("INDIA") && !srcVal.contains("FATHER")) {
                metaData = "NAME";

            }

            metadataMap.put(metaData, tgtVal.trim());
        } catch (Exception e) {
            Log.i(TAG,e.getMessage());
            Log.i(TAG,e.getMessage());
            throw new Exception(e);
        }
    }

    private String getFormatedDate(String datevalue) throws Exception {
        try {
            datevalue = (datevalue != null && !datevalue.isEmpty()) ? datevalue.trim() : "";

            if (datevalue.matches("\\d{4}")) {
                //This block will execute when we have only year in the aadhaar caed
                return "01-01-" + datevalue;
            } else {
                return DateUtils.aAdhaarDateFormated(datevalue);
            }
        } catch (Exception execption) {
            Log.i(TAG, execption.getMessage());
            throw new Exception(execption);
        }
    }

    private Matcher getPatternMatcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher patternMatcher = pattern.matcher(value);

        return patternMatcher;
    }
}
