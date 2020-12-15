package com.example.robolectrictestframework.model;

public class OcrDataRecord {

    private String userName;
    private String mobile;
    private String ocrImageText;
    private String aadharNo;
    private String name;
    private String dob;
    private String gender;
    private String imageText;
    private String mimeType;

    public OcrDataRecord(String userName, String mobile, String ocrImageText, String aadharNo, String name, String dob, String gender, String imageText, String mimeType) {
        this.userName = userName;
        this.mobile = mobile;
        this.ocrImageText = ocrImageText;
        this.aadharNo = aadharNo;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.imageText = imageText;
        this.mimeType = mimeType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOcrImageText() {
        return ocrImageText;
    }

    public void setOcrImageText(String ocrImageText) {
        this.ocrImageText = ocrImageText;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageText() {
        return imageText;
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "OcrDataRecord{" +
                "userName='" + userName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", ocrImageText='" + ocrImageText + '\'' +
                ", aadharNo='" + aadharNo + '\'' +
                ", name='" + name + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", imageText='" + imageText + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
