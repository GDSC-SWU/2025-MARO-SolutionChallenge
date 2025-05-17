package me.seungeun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
// Automatically generates constructor-based dependency injection
public class VaccineVerificationService {

    // AIClient handles communication with the AI server
    private final AIClient aiClient;

    /**
     * Verifies vaccine information and image using AI model.
     *
     * @param vaccine_ko Vaccine name in Korean
     * @param vaccine_en Vaccine name in English
     * @param period Vaccination period or validity duration
     * @param image Uploaded vaccine certificate image
     * @return true if verification is successful; false otherwise
     */
    public boolean verify(String vaccine_ko, String vaccine_en, String period, MultipartFile image) {
        try {
            // Sends request to FastAPI server via AIClient
            return aiClient.sendToAI(vaccine_ko, vaccine_en, period, image);
        } catch (IOException e) {
            // Handles IOException such as image processing failure
            throw new RuntimeException("AI server request failed", e);
        }
    }
}
