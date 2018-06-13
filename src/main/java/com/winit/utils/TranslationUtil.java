package com.winit.utils;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

/**
 * Created by yf on 2017-12-11.
 */
public class TranslationUtil {

    public static void main(String... args) {
        // Create a service object
        //
        // If no explicit credentials or API key are set, requests are authenticated using Application
        // Default Credentials if available; otherwise, using an API key from the GOOGLE_API_KEY
        // environment variable
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        // Text of an "unknown" language to detect and then translate into English
        final String mysteriousText = "Hola Mundo";

        // Detect the language of the mysterious text
        Detection detection = translate.detect(mysteriousText);
        String detectedLanguage = detection.getLanguage();

        // Translate the mysterious text to English
        Translation translation = translate.translate(
                mysteriousText,
                TranslateOption.sourceLanguage(detectedLanguage),
                TranslateOption.targetLanguage("en"));
        System.out.println(translation.getTranslatedText());
    }
}
