package com.example.luca.ocrecog;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOCR implements OCRWrapper {
    public static final String ITA = "ita";
    public static final String ENG = "eng";
    private String mDataPath;
    private String mLanguage;
    private int minConfidence = 60;

    private TessBaseAPI tessBaseAPI;

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


    /**
     *
     * @param img The image in a Bitmap format
     * @return  text recognized from the OCR, empty text if img is null
     */
    public String getTextFromImg(Bitmap img) {
        if(img == null)
            return "";

        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(mDataPath, mLanguage);
        tessBaseAPI.setImage(img);
        String text = tessBaseAPI.getUTF8Text();

        int[] wordsConfidence = tessBaseAPI.wordConfidences();
        text = filterText(text, wordsConfidence, this.minConfidence);

        tessBaseAPI.end();
        return text;
    }

    /**
     *
     * @param img The image in a Bitmap format
     * @return  text recognized from the OCR plus the mean confidence of the recognition, empty text if img is null
     */
    public String getTextWithConfidenceFromImg(Bitmap img) {
        if(img == null)
            return "";


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

    /**
     *
     * @param minConfidence can be 0 to 100, min confidence to include a word inside the recognized text
     */
    public void setMinConfidence(int minConfidence) {
        this.minConfidence = minConfidence;
    }

    /**
     *
     * @param language select which trained data the OCR will use
     */
    public void setLanguage(String language) {
        this.mLanguage = language;
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
