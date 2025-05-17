package me.seungeun.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VaccineVerificationRequestDto {
    private String vaccine_ko;      // Vaccine name in Korean
    private String vaccine_en;      // Vaccine name in English
    private String period;          // Vaccination or validity period
    private MultipartFile image;    // Uploaded vaccine certificate image file
}
