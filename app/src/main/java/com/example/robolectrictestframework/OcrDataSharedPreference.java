package com.example.robolectrictestframework;

import android.content.Context;
import android.content.SharedPreferences;


public class OcrDataSharedPreference {

    private static final String SETTINGS_NAME = "kolagaram_settings";
    private static OcrDataSharedPreference sSharedPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean mBulkUpdate = false;
    public static byte[] KOLOGARAM_IMAGE;
    /**
     * Enum representing your setting names or key for your setting.
     */
    public enum Key {



        NAME_KEY,
        MOBILE_NUM_KEY,
        IMAGE_TEXT,
        AADHAR_NO_KEY,
        NAME_ON_AADHAR_KEY,
        DOB_ON_AADHAR_KEY,
        GENDER_ON_AADHAR_KEY,
        IMAGE_PATH
    }

    private OcrDataSharedPreference(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }


    public static OcrDataSharedPreference getInstance(Context context) {
        if (sSharedPrefs == null) {
            sSharedPrefs = new OcrDataSharedPreference(context.getApplicationContext());
        }
        return sSharedPrefs;
    }

    public static OcrDataSharedPreference getInstance() {
        if (sSharedPrefs != null) {
            return sSharedPrefs;
        }

        //Option 1:
        // throw new IllegalArgumentException("Should use getInstance(Context) at least once before using this method.");

        //Option 2:
        // Alternatively, you can create a new instance here
        // with something like this:
        return getInstance(TestingAadhaarOcrApp.getAppContext());
    }
    public void put(OcrDataSharedPreference.Key key, String val) {
        doEdit();
        mEditor.putString(key.name(), val);
        doCommit();
    }

    public void put(OcrDataSharedPreference.Key key, int val) {
        doEdit();
        mEditor.putInt(key.name(), val);
        doCommit();
    }

    public void put(OcrDataSharedPreference.Key key, boolean val) {
        doEdit();
        mEditor.putBoolean(key.name(), val);
        doCommit();
    }

    public void put(OcrDataSharedPreference.Key key, float val) {
        doEdit();
        mEditor.putFloat(key.name(), val);
        doCommit();
    }

    /**
     * Convenience method for storing doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The enum of the preference to store.
     * @param val The new value for the preference.
     */
    public void put(OcrDataSharedPreference.Key key, double val) {
        doEdit();
        mEditor.putString(key.name(), String.valueOf(val));
        doCommit();
    }

    public void put(OcrDataSharedPreference.Key key, long val) {
        doEdit();
        mEditor.putLong(key.name(), val);
        doCommit();
    }

    public String getString(OcrDataSharedPreference.Key key, String defaultValue) {
        return mPref.getString(key.name(), defaultValue);
    }

    public String getString(OcrDataSharedPreference.Key key) {
        return mPref.getString(key.name(), null);
    }

    public int getInt(OcrDataSharedPreference.Key key) {
        return mPref.getInt(key.name(), 0);
    }

    public int getInt(OcrDataSharedPreference.Key key, int defaultValue) {
        return mPref.getInt(key.name(), defaultValue);
    }

    public long getLong(OcrDataSharedPreference.Key key) {
        return mPref.getLong(key.name(), 0);
    }

    public long getLong(OcrDataSharedPreference.Key key, long defaultValue) {
        return mPref.getLong(key.name(), defaultValue);
    }

    public float getFloat(OcrDataSharedPreference.Key key) {
        return mPref.getFloat(key.name(), 0);
    }

    public float getFloat(OcrDataSharedPreference.Key key, float defaultValue) {
        return mPref.getFloat(key.name(), defaultValue);
    }

    /**
     * Convenience method for retrieving doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The enum of the preference to fetch.
     */
    public double getDouble(OcrDataSharedPreference.Key key) {
        return getDouble(key, 0);
    }

    /**
     * Convenience method for retrieving doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The enum of the preference to fetch.
     */
    public double getDouble(OcrDataSharedPreference.Key key, double defaultValue) {
        try {
            return Double.valueOf(mPref.getString(key.name(), String.valueOf(defaultValue)));
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public boolean getBoolean(OcrDataSharedPreference.Key key, boolean defaultValue) {
        return mPref.getBoolean(key.name(), defaultValue);
    }

    public boolean getBoolean(OcrDataSharedPreference.Key key) {
        return mPref.getBoolean(key.name(), false);
    }

    /**
     * Remove keys from SharedPreferences.
     *
     * @param keys The enum of the key(s) to be removed.
     */
    public void remove(OcrDataSharedPreference.Key... keys) {
        doEdit();
        for (OcrDataSharedPreference.Key key : keys) {
            mEditor.remove(key.name());
        }
        doCommit();
    }
    /**
     * Remove all keys from SharedPreferences.
     */
    public void clear() {
        doEdit();
        mEditor.clear();
        doCommit();
    }

    public void edit() {
        mBulkUpdate = true;
        mEditor = mPref.edit();
    }

    public void commit() {
        mBulkUpdate = false;
        mEditor.commit();
        mEditor = null;
    }

    private void doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref.edit();
        }
    }

    private void doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }
}
