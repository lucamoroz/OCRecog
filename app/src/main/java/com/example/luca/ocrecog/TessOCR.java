package com.example.luca.ocrecog;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOCR implements OCRWrapper {
    public static final String ITA = "ita";
    public static final String ENG = "eng";
    private String mDataPath;
    private String mLanguage;

    TessBaseAPI tessBaseAPI;
    public TessOCR(String dataPath, String language) {
        mDataPath = dataPath;
        mLanguage = language;
    }
    public void changeLanguege(String languege) {
        mLanguage = languege;
    }
    public void changeDataPath(String dataPath) {
        mDataPath = dataPath;
    }

    public String getTextFromImg(Bitmap img) {
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(mDataPath, mLanguage);
        tessBaseAPI.setImage(img);
        String recogText = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();
        return recogText;
    }
    public String getTextWithConfidenceFromImg(Bitmap img) {
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(mDataPath, mLanguage);
        tessBaseAPI.setImage(img);
        String recogText = tessBaseAPI.getUTF8Text();
        int meanConfidence = tessBaseAPI.meanConfidence();
        tessBaseAPI.end();
        return recogText + "\n" + "Mean confidence: " + meanConfidence;
    }
}
