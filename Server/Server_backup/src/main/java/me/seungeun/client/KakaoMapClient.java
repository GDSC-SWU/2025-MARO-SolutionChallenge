package me.seungeun.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.seungeun.dto.RegionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoMapClient {


    // Create RestTemplate for making HTTP requests
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    //Kakao REST API key (injected from config)

    public RegionInfo getRegionInfo(double lat, double lng) {
        // Build Kakao API URL for coordinate-to-region lookup
        String url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json"
                + "?x=" + lng + "&y=" + lat;


        // Add Authorization header with API key
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Make GET request to Kakao API and parse response to KakaoResponse
        ResponseEntity<KakaoResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KakaoResponse.class
        );

        // Convert first result in response to RegionInfo if available
        if (response.getBody() != null && response.getBody().getDocuments().size() > 0) {
            return response.getBody().getDocuments().get(0).toRegionInfo();
        } else {

            // Throw error if no region info found
            throw new RuntimeException("Location information search failed");
        }
    }

    @lombok.Data
    public static class KakaoResponse {
        private java.util.List<Document> documents;

        @lombok.Data
        public static class Document {
            private String region_1depth_name;
            // Province/City name (e.g., Seoul)
            private String region_2depth_name;
            // District name (e.g., Gangnam-gu)

            // Convert to RegionInfo DTO
            public RegionInfo toRegionInfo() {
                return new RegionInfo(region_1depth_name, region_2depth_name);
            }
        }
    }
}

