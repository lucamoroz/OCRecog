package com.example.luca.ocrecog;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOCR implements OCRWrapper {
    public static final String ITA = "ita";
    public static final String ENG = "eng";
    private String mDataPath;
    private String mLanguage;
    private int minConfidence = 60;

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
        String text = tessBaseAPI.getUTF8Text();

        int[] wordsConfidence = tessBaseAPI.wordConfidences();
        text = filterText(text, wordsConfidence, this.minConfidence);

        tessBaseAPI.end();
        return text;
    }
    public String getTextWithConfidenceFromImg(Bitmap img) {
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(mDataPath, mLanguage);
        tessBaseAPI.setImage(img);
        String text = tessBaseAPI.getUTF8Text();

        int[] wordsConfidence = tessBaseAPI.wordConfidences();
        text = filterText(text, wordsConfidence, this.minConfidence);

        int meanConfidence = tessBaseAPI.meanConfidence();
        tessBaseAPI.end();
        return text + "\n" + "Mean confidence: " + meanConfidence;
    }

    public void setMinConfidence(int minConfidence) {
        this.minConfidence = minConfidence;
    }

    private String filterText(String text, int[] wordsConfidence, int minConfidence) {
        String[] words = text.split(" ");

        if(words.length != wordsConfidence.length) {
            Log.i("TextFilter", "Error filtering text: lenghts don't correspond. N words: " + Integer.toString(words.length) + " N confidences: " + Integer.toString(wordsConfidence.length));
        }


        String filteredText = "";

        int count = Math.min(words.length, wordsConfidence.length);

        for(int i = 0; i < count; i++) {
            if(wordsConfidence[i] > minConfidence)
                filteredText += words[i] + " ";
        }

        if(count < words.length) {
            for(int i = count; i < words.length; i++)
                filteredText += words[i] + " ";
        }

        return filteredText;
    }

}
