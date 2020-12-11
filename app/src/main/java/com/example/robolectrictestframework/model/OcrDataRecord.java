package com.example.robolectrictestframework.model;

public class OcrDataRecord {

    private String name;
    private String mobile;
    private String ocrImageText;


    public OcrDataRecord(String name, String mobile, String ocrImageText) {
        this.name = name;
        this.mobile = mobile;
        this.ocrImageText = ocrImageText;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOcrImageText() {
        return ocrImageText;
    }

    public void setOcrImageText(String ocrImageText) {
        this.ocrImageText = ocrImageText;
    }
}
