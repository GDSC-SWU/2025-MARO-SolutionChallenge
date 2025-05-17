package me.seungeun.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.seungeun.dto.HospitalDto;
import me.seungeun.dto.TranslationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class TranslateClient {

    // RestTemplate instance for HTTP calls
    private final RestTemplate restTemplate = new RestTemplate();

    // Google Cloud Translation API key injected from application properties
    @Value("${translate.api.key}")
    private String apiKey;

    /**
     * Translates multiple text fields of a HospitalDto into the target language synchronously.
     * @param dto HospitalDto containing original texts
     * @param targetLanguage target language code (e.g., "en", "ko")
     * @return Map with keys as field names and values as translated texts
     */
    public Map<String, String> translateAll(HospitalDto dto, String targetLanguage) {
        Map<String, String> translations = new HashMap<>();

        translations.put("name", translate(dto.getName(), targetLanguage));
        translations.put("address", translate(dto.getAddress(), targetLanguage));
        translations.put("phone", translate(dto.getPhone(), targetLanguage));
        translations.put("weekday", translate(dto.getWeekday(), targetLanguage));

        return translations;
    }

    /**
     * Asynchronously translates all text fields of a HospitalDto.
     * @param hospital HospitalDto object
     * @param targetLang target language code
     * @return CompletableFuture wrapping a Map of translated fields
     */
    public CompletableFuture<Map<String, String>> translateAllAsync(HospitalDto hospital, String targetLang) {
        return CompletableFuture.supplyAsync(() -> translateAll(hospital, targetLang));
    }

    /**
     * Translates a single text string to the target language.
     * @param text text to translate
     * @param targetLanguage language code to translate into
     * @return translated text or original text if empty or on failure
     */
    public String translateText(String text, String targetLanguage) {
        return translate(text, targetLanguage);
    }

    /**
     * Core translation logic calling Google Cloud Translation API.
     * Sends POST request with JSON payload and parses the translation response.
     * @param text text to translate
     * @param targetLanguage language code (e.g., "en", "ko")
     * @return translated text or original text if empty or on failure
     */
    public String translate(String text, String targetLanguage) {
        if (text == null || text.isBlank()) return "";

        try {
            // Build URI for Google Translate API endpoint with API key query param
            URI uri = UriComponentsBuilder
                    .fromUriString("https://translation.googleapis.com/language/translate/v2")
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            // Prepare request body with text, target language, and format
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", text);
            requestBody.put("target", targetLanguage);
            requestBody.put("format", "text");

            // Set headers specifying JSON content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Wrap request body and headers into HttpEntity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Send POST request and map response to TranslationResponse DTO
            TranslationResponse response = restTemplate.postForObject(uri, entity, TranslationResponse.class);

            // Extract translated text if response is valid
            if (response != null && response.getData() != null) {
                List<TranslationResponse.Translation> translations = response.getData().getTranslations();
                if (translations != null && !translations.isEmpty()) {
                    return translations.get(0).getTranslatedText();
                }
            }
        } catch (Exception e) {
            log.error("translation failed: {}", e.getMessage());
        }

        // Return original text if translation fails or input is empty
        return text;
    }
}
