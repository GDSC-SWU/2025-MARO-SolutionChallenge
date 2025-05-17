package me.seungeun.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.seungeun.client.KakaoMapClient;
import me.seungeun.dto.RegionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaccineHospitalCacheService {

    // RestTemplate instance for external API calls
    private final RestTemplate restTemplate = new RestTemplate();

    // KakaoMapClient injected to obtain administrative region info by lat/lng
    private final KakaoMapClient kakaoMapClient;

    // Cache storing vaccine hospital info keyed by normalized hospital name
    private final Map<String, VaccineInfo> vaccineHospitalMap = new HashMap<>();

    // API key for public data portal, read from application.yml
    @Value("${publicdata.service.key}")
    private String serviceKey;

    // Reusable ObjectMapper instance for JSON parsing
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Mapping of city names to administrative codes (e.g., Seoul Special City -> 1100000000)
    private static final Map<String, String> brtcCdMap = Map.of(
            "서울특별시", "1100000000",
            "부산광역시", "2600000000"
    );

    // Mapping of district names to administrative codes (e.g., Jongno-gu -> 11110)
    private static final Map<String, String> sggCdMap = Map.of(
            "종로구", "11110",
            "중구", "11140",
            "강남구", "11680"
    );

    // Normalize hospital name: convert to lowercase and remove whitespace
    private String normalize(String name) {
        if (name == null) return "";
        return name.toLowerCase().replaceAll("\\s+", "");
    }

    /**
     * Initialize the cache by fetching vaccine hospitals from public data API
     * according to the provided latitude and longitude.
     * Clears previous cache and stores fresh data.
     * @param lat latitude coordinate
     * @param lng longitude coordinate
     */
    public void initializeCache(double lat, double lng) {
        List<VaccineInfo> vaccineInfos = fetchHospitalsByLocation(lat, lng);
        vaccineHospitalMap.clear();
        for (VaccineInfo info : vaccineInfos) {
            vaccineHospitalMap.put(normalize(info.getCenterName()), info);
        }
        log.info("VaccineHospitalCache initialized with {} entries", vaccineHospitalMap.size());
    }

    /**
     * Runs once after bean construction.
     * Initializes cache with default coordinate (center of Seoul).
     */
    @PostConstruct
    public void init() {
        initializeCache(37.5665, 126.9780);
    }

    /**
     * Finds the best matching vaccine hospital info by computing Levenshtein
     * distance between input hospital name and cached hospital names.
     * @param hospitalName hospital name to search for
     * @return best matching VaccineInfo or null if none found
     */
    public VaccineInfo getBestMatchingHospital(String hospitalName) {
        String normalizedName = normalize(hospitalName);
        vaccineHospitalMap.forEach((key, value) -> {
            int dist = levenshtein(normalizedName, normalize(key));
            log.info("Comparing '{}' to '{}', distance: {}", normalizedName, normalize(key), dist);
        });

        return vaccineHospitalMap.entrySet().stream()
                .min(Comparator.comparingInt(e -> levenshtein(normalizedName, normalize(e.getKey()))))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    /**
     * Computes Levenshtein distance between two strings.
     * This is used to determine similarity by minimum edit distance.
     * @param a first string
     * @param b second string
     * @return edit distance between a and b
     */
    public static int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
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

    /**
     * Uses KakaoMapClient to get administrative region by lat/lng,
     * then calls public data portal API to fetch vaccine hospital list in JSON format.
     * @param lat latitude coordinate
     * @param lng longitude coordinate
     * @return list of VaccineInfo, or mock data if API call fails
     */
    public List<VaccineInfo> fetchHospitalsByLocation(double lat, double lng) {
        try {
            RegionInfo region = kakaoMapClient.getRegionInfo(lat, lng);
            String brtcCd = brtcCdMap.get(region.getSi());
            String sggCd = sggCdMap.get(region.getGu());

            if (brtcCd == null || sggCd == null) {
                log.warn("Failed to map administrative codes: {}, {}", region.getSi(), region.getGu());
                return List.of();
            }

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://apis.data.go.kr/1790387/orglist3/getOrgList3")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("numOfRows", 100)
                    .queryParam("pageNo", 1)
                    .queryParam("returnType", "json")
                    .queryParam("brtcCd", brtcCd)
                    .queryParam("sggCd", sggCd)
                    .toUriString();

            log.info("Location-based hospital request: {}", url);

            String responseStr = restTemplate.getForObject(url, String.class);

            // Guard against XML response (API key or parameters issues)
            if (responseStr != null && responseStr.trim().startsWith("<")) {
                log.error("API response is XML, not JSON. Check API key or request parameters.");
                log.debug("XML response: {}", responseStr);
                return loadMockVaccineInfo();
            }

            VaccineApiResponse parsed = objectMapper.readValue(responseStr, VaccineApiResponse.class);

            return parsed.getData();

        } catch (Exception e) {
            log.error("Failed to fetch vaccine hospital information from public data API", e);
            return loadMockVaccineInfo();
        }
    }

    // POJO for API response wrapper
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class VaccineApiResponse {
        private List<VaccineInfo> data; // hospital list under data field
    }

    // POJO for vaccine hospital info
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VaccineInfo {
        private String centerName;    // hospital name
        private String address;       // address
        private String tel;           // phone number

        @JsonProperty("vaccine")
        private List<String> vaccines; // available vaccines
        private double lat;           // latitude
        private double lng;           // longitude
    }

    /**
     * Loads mock vaccine hospital data from JSON file
     * under resources/mock/vaccine_sample.json in case of API failure.
     * @return list of VaccineInfo mock data
     */
    private List<VaccineInfo> loadMockVaccineInfo() {
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream("mock/vaccine_sample.json");
            if (inputStream == null) {
                log.warn("Mock JSON file not found.");
                return List.of();
            }

            List<VaccineInfo> list = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, VaccineInfo.class)
            );
            log.info("Mock vaccine info loaded successfully: {} records", list.size());
            return list;

        } catch (Exception e) {
            log.error("Failed to read mock vaccine info file", e);
            return List.of();
        }

    }
}
