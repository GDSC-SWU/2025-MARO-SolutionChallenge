package me.seungeun.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.seungeun.dto.HospitalDto;
import me.seungeun.dto.publicdata.LocationRequestDto;
import me.seungeun.dto.PlaceIdRequestDto;
import me.seungeun.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks the class as a REST controller
@RequestMapping("/api/hospitals") // Sets base path for hospital-related endpoints
@Slf4j
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService; // Service for handling hospital logic

    /**
     * Handles request for list of nearby hospitals.
     * @param request DTO containing location info (latitude, longitude)
     * @return list of HospitalDto and HTTP 200 response
     */
    @PostMapping("/nearby")
    public ResponseEntity<List<HospitalDto>> getNearbyHospitals(@RequestBody LocationRequestDto request) {
        log.info("Received nearby hospital request: {}", request);
        return ResponseEntity.ok(hospitalService.findNearbyHospitals(request));
    }

    /**
     * Handles request for translated list of nearby hospitals.
     * @param request location info DTO
     * @param targetLang target language code for translation (e.g. "en", "ko")
     * @return translated hospital list and HTTP 200 response
     */
    @PostMapping("/nearby/translated")
    public ResponseEntity<?> getTranslatedNearbyHospitals(
            @RequestBody LocationRequestDto request,
            @RequestParam String targetLang) {

        log.info("Received translated nearby hospitals request: {}, lang={}", request, targetLang);
        return ResponseEntity.ok(hospitalService.findTranslatedNearbyHospitals(request, targetLang));
    }

    /**
     * Handles request for details of a specific hospital.
     * @param request DTO containing placeId
     * @return HospitalDto with detailed info and HTTP 200 response
     */
    @PostMapping("/details")
    public ResponseEntity<HospitalDto> getHospitalDetails(@RequestBody PlaceIdRequestDto request) {
        log.info("Received hospital details request: {}", request);
        return ResponseEntity.ok(hospitalService.getHospitalDetails(request));
    }

    /**
     * Handles request for translated details of a specific hospital.
     * @param request DTO containing placeId
     * @param targetLang target language code
     * @return translated hospital details and HTTP 200 response
     */
    @PostMapping("/details/translated")
    public ResponseEntity<?> getTranslatedHospitalDetails(
            @RequestBody PlaceIdRequestDto request,
            @RequestParam String targetLang) {

        log.info("Received translated hospital details request: {}, lang={}", request, targetLang);
        return ResponseEntity.ok(hospitalService.getTranslatedHospitalDetails(request, targetLang));
    }
}
