package me.seungeun.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ChatService {

    // RestTemplate instance used to send HTTP requests
    private final RestTemplate restTemplate;

    // FastAPI server URL injected from application.yml
    @Value("${fastapi.model.url}")
    private String fastapiModelUrl;

    // Constructor-based injection for RestTemplate
    public ChatService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    /**
     * Sends text question and language info to FastAPI /chat endpoint and returns AI response.
     *
     * @param user_text User's question text
     * @param user_lang Requested language code (e.g., "ko", "en")
     * @return AI-generated answer string
     */
    public String getAnswerFromAI(String user_text, String user_lang) {
        try {
            // Prepare request body as form data
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("user_text", user_text);
            requestBody.add("user_lang", user_lang);

            // Set Content-Type header to application/x-www-form-urlencoded
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create HttpEntity with body and headers
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Send POST request to FastAPI /chat endpoint
            ResponseEntity<String> response = restTemplate.postForEntity(
                    fastapiModelUrl + "/chat", request, String.class
            );

            // Debug logs for request content
            System.out.println("Request Body: " + requestBody.toString());
            System.out.println("Request: " + request);

            // Return response body
            return response.getBody();

        } catch (Exception e) {
            System.err.println("Failed to call AI model: " + e.getMessage());
            return "Failed to get AI answer.";
        }
    }

    /**
     * Sends text, image, and language info as multipart/form-data to FastAPI /vision endpoint and returns AI response.
     *
     * @param user_text Question text
     * @param image Uploaded image file
     * @param language Requested language code
     * @return AI-generated answer string
     */
    public String getAnswerWithImage(String user_text, MultipartFile image, String language) {
        try {
            // Set headers for multipart/form-data request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Prepare multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("user_text", user_text);
            body.add("user_lang", language);

            // Convert MultipartFile to ByteArrayResource and add to request body if present
            if (image != null && !image.isEmpty()) {
                body.add("image", new ByteArrayResource(image.getBytes()) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                });
            }

            // Create HttpEntity with multipart body and headers
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send POST request to FastAPI /vision endpoint
            ResponseEntity<String> response = restTemplate.postForEntity(
                    fastapiModelUrl + "/vision", requestEntity, String.class
            );

            // Return response body
            return response.getBody();

        } catch (IOException e) {
            System.err.println("An error occurred while processing the image: " + e.getMessage());
            return "An error occurred while processing the image.";
        } catch (Exception e) {
            System.err.println("FastAPI image-based question API call failed: " + e.getMessage());
            return "Failed to get AI answer.";
        }
    }
}
