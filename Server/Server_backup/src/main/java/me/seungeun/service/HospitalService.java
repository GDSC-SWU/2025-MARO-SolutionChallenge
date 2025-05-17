package me.seungeun.service;

import lombok.extern.slf4j.Slf4j;
import me.seungeun.cache.VaccineHospitalCacheService;
import me.seungeun.cache.VaccineHospitalCacheService.VaccineInfo;
import me.seungeun.client.GooglePlaceClient;
import me.seungeun.client.TranslateClient;
import me.seungeun.dto.HospitalDto;
import me.seungeun.dto.PlaceIdRequestDto;
import me.seungeun.dto.publicdata.LocationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HospitalService {

    private final GooglePlaceClient googlePlaceClient;
    private final TranslateClient translateClient;
    private final VaccineHospitalCacheService vaccineHospitalCacheService;

    @Autowired
    public HospitalService(GooglePlaceClient googlePlaceClient,
                           TranslateClient translateClient,
                           VaccineHospitalCacheService vaccineHospitalCacheService) {
        this.googlePlaceClient = googlePlaceClient;
        this.translateClient = translateClient;
        this.vaccineHospitalCacheService = vaccineHospitalCacheService;
    }

    // Mapping of English hospital names to Korean for matching with cached data
    private static final Map<String, String> hospitalNameMap = Map.ofEntries(
            Map.entry("Severance Hospital", "연세대학교 세브란스병원"),
            Map.entry("Seoul Red Cross Hospital", "서울적십자병원"),
            Map.entry("Seoul National University Dental Hospital", "서울대학교치과병원"),
            Map.entry("Samsung Medical Center", "삼성서울병원"),
            Map.entry("Asan Medical Center", "서울아산병원"),
            Map.entry("Kangbuk Samsung Hospital", "강북삼성병원")
    );

    // Find nearby hospitals with vaccine info and translated fields
    public List<HospitalDto> findNearbyHospitals(LocationRequestDto request) {
        double lat = request.getLat();
        double lng = request.getLng();

        List<HospitalDto> hospitals = googlePlaceClient.getNearbyHospitals(lat, lng);
        List<VaccineInfo> vaccineInfos = vaccineHospitalCacheService.fetchHospitalsByLocation(lat, lng);
        if (vaccineInfos == null) {
            vaccineInfos = List.of();
        }
        final List<VaccineInfo> finalVaccineInfos = vaccineInfos;

        String contactMessage = "병원에 문의해주세요";
        String translatedContactMessage = translateClient.translate(contactMessage, request.getLanguage());

        return hospitals.stream()
                .limit(10)
                .map(h -> {
                    try {
                        String originalName = h.getName();
                        String mappedName = hospitalNameMap.getOrDefault(originalName, originalName);
                        String googleName = normalize(mappedName);

                        VaccineInfo match = finalVaccineInfos.stream()
                                .filter(info -> {
                                    String googleNameNorm = normalize(mappedName);
                                    String publicNameNorm = normalize(info.getCenterName());

                                    log.debug("Comparing hospital names - Google: {}, Public: {}", googleNameNorm, publicNameNorm);

                                    return googleNameNorm.contains(publicNameNorm) || publicNameNorm.contains(googleNameNorm);
                                })
                                .findFirst()
                                .orElse(null);

                        if (match != null && match.getVaccines() != null && !match.getVaccines().isEmpty()) {
                            List<String> translatedVaccines = translateVaccines(match.getVaccines(), request.getLanguage());
                            h.setVaccines(translatedVaccines);
                        } else {
                            h.setVaccines(List.of(translatedContactMessage));
                        }

                        Map<String, String> translated = translateClient.translateAll(h, request.getLanguage());
                        return HospitalDto.from(h, translated);

                    } catch (Exception e) {
                        log.error("Error during hospital processing: " + h.getName(), e);
                        return HospitalDto.from(h, Map.of("name", h.getName()));
                    }
                })
                .collect(Collectors.toList());
    }

    // Find nearby hospitals with translation into target language
    public List<HospitalDto> findTranslatedNearbyHospitals(LocationRequestDto request, String targetLang) {
        double lat = request.getLat();
        double lng = request.getLng();

        List<HospitalDto> hospitals = googlePlaceClient.getNearbyHospitals(lat, lng);
        List<VaccineInfo> vaccineInfos = vaccineHospitalCacheService.fetchHospitalsByLocation(lat, lng);
        if (vaccineInfos == null) {
            vaccineInfos = List.of();
        }
        final List<VaccineInfo> finalVaccineInfos = vaccineInfos;

        String contactMessage = "병원에 문의해주세요";
        String translatedContactMessage = translateClient.translate(contactMessage, request.getLanguage());

        return hospitals.stream()
                .map(h -> {
                    try {
                        String originalName = h.getName();
                        String mappedName = hospitalNameMap.getOrDefault(originalName, originalName);

                        VaccineInfo match = findClosestMatch(mappedName, finalVaccineInfos);

                        if (match != null) {
                            List<String> translatedVaccines = translateVaccines(match.getVaccines(), request.getLanguage());
                            h.setVaccines(translatedVaccines);
                        } else {
                            h.setVaccines(List.of(translatedContactMessage));
                        }

                        Map<String, String> translated = translateClient.translateAll(h, targetLang);
                        return HospitalDto.from(h, translated);

                    } catch (Exception e) {
                        log.error("Error during hospital processing: " + h.getName(), e);
                        return HospitalDto.from(h, Map.of("name", h.getName()));
                    }
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    // Get detailed hospital info including vaccine info and translations
    public HospitalDto getHospitalDetails(PlaceIdRequestDto request) {
        HospitalDto detail = googlePlaceClient.getPlaceDetails(request.getPlaceId());

        log.info("Requesting vaccine info for hospital: {}", detail.getName());

        String googleName = detail.getName();
        String mappedName = hospitalNameMap.getOrDefault(googleName, googleName);

        VaccineInfo cached = vaccineHospitalCacheService.getBestMatchingHospital(normalize(mappedName));
        log.info("Cached vaccine info for '{}': {}", mappedName, cached != null ? "FOUND" : "NOT FOUND");

        String contactMessage = "병원에 문의해주세요";
        String translatedContactMessage = translateClient.translate(contactMessage, request.getLanguage());

        if (cached != null) {
            List<String> translatedVaccines = translateVaccines(cached.getVaccines(), request.getLanguage());
            detail.setVaccines(translatedVaccines);
        } else {
            detail.setVaccines(List.of(translatedContactMessage));
        }

        Map<String, String> translated = translateClient.translateAll(detail, request.getLanguage());
        return HospitalDto.from(detail, translated);
    }

    // Get translated detailed hospital info
    public HospitalDto getTranslatedHospitalDetails(PlaceIdRequestDto request, String targetLang) {
        HospitalDto detail = googlePlaceClient.getPlaceDetails(request.getPlaceId());

        log.info("Requesting vaccine info for hospital: {}", detail.getName());

        String googleName = detail.getName();
        String mappedName = hospitalNameMap.getOrDefault(googleName, googleName);

        VaccineInfo cached = vaccineHospitalCacheService.getBestMatchingHospital(normalize(mappedName));
        if (cached != null) {
            log.info("Found vaccine info for hospital {}: {}", mappedName, cached.getVaccines());
        } else {
            log.warn("No vaccine info found for hospital {}", mappedName);
        }

        String contactMessage = "병원에 문의해주세요";
        String translatedContactMessage = translateClient.translate(contactMessage, targetLang);

        if (cached != null) {
            List<String> translatedVaccines = translateVaccines(cached.getVaccines(), targetLang);
            detail.setVaccines(translatedVaccines);
        } else {
            detail.setVaccines(List.of(translatedContactMessage));
        }

        try {
            Map<String, String> translated = translateClient.translateAll(detail, targetLang);
            return HospitalDto.from(detail, translated);
        } catch (Exception e) {
            log.error("Hospital detailed translation failed: {}", detail.getName(), e);
            return HospitalDto.from(detail, Map.of("name", detail.getName()));
        }
    }

    // Normalize hospital names: lowercase and remove whitespace
    private String normalize(String name) {
        if (name == null) return "";
        return name.toLowerCase().replaceAll("\\s+", "");
    }

    // Find closest matching VaccineInfo by Levenshtein distance
    private VaccineInfo findClosestMatch(String googleName, List<VaccineInfo> candidates) {
        String normalizedGoogle = normalize(googleName);
        return candidates.stream()
                .min(Comparator.comparingInt(info -> levenshtein(normalizedGoogle, normalize(info.getCenterName()))))
                .orElse(null);
    }

    // Simple Levenshtein distance implementation
    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    // Translate list of vaccine names to target language
    private List<String> translateVaccines(List<String> vaccines, String targetLang) {
        if (vaccines == null || vaccines.isEmpty()) return List.of();
        return vaccines.stream()
                .map(v -> translateClient.translate(v, targetLang))
                .collect(Collectors.toList());
    }
}
