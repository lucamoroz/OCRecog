package com.example.luca.ocrecog;
import android.graphics.Bitmap;

public interface OCRWrapper {
    /**
     * Wrapper for OCR library. Extract a text from a given image.
     * @param img The image in a Bitmap format
     * @return The String of the text recognized (empty String if nothing is recognized)
     */
    String getTextFromImg(Bitmap img);
}