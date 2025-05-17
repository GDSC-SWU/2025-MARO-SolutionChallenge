package me.seungeun.controller;

import lombok.RequiredArgsConstructor;
import me.seungeun.service.VaccineVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vaccine")
public class VaccineVerificationController {

    private final VaccineVerificationService verificationService;

    // Vaccine certificate verification API endpoint
    // vaccine_ko: vaccine name in Korean
    // vaccine_en: vaccine name in English
    // period: vaccination period or validity duration
    // image: uploaded vaccine certificate image file
    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyVaccineCertificate(
            @RequestParam String vaccine_ko,
            @RequestParam String vaccine_en,
            @RequestParam String period,
            @RequestParam MultipartFile image
    ) {
        // Pass data to service to check certificate authenticity
        boolean result = verificationService.verify(vaccine_ko, vaccine_en, period, image);
        // Return verification result (true/false) with HTTP 200 OK
        return ResponseEntity.ok(result);
    }
}
