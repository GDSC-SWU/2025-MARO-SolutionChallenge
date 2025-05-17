package me.seungeun.dto;

import java.util.List;

public class TranslationResponse {

    // Top-level field that holds the translation data
    private Data data;

    // Getter for the 'data' field
    public Data getData() {
        return data;
    }

    // Setter for the 'data' field
    public void setData(Data data) {
        this.data = data;
    }

    // Inner class representing the structure of the 'data' object in the response
    public static class Data {

        // List of individual translation results
        private List<Translation> translations;

        // Getter for the 'translations' list
        public List<Translation> getTranslations() {
            return translations;
        }

        // Setter for the 'translations' list
        public void setTranslations(List<Translation> translations) {
            this.translations = translations;
        }
    }

    // Inner class representing a single translation result
    public static class Translation {

        // Translated text from the API
        private String translatedText;

        // Getter for 'translatedText'
        public String getTranslatedText() {
            return translatedText;
        }

        // Setter for 'translatedText'
        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }
    }
}
