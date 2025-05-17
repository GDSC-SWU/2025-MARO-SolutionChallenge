package me.seungeun.service;

import me.seungeun.dto.VaccineInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccinationService {

    // API service key injected from application.yml
    @Value("${publicdata.service.key}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    public VaccinationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the list of vaccines available for a specific hospital (placeId).
     *
     * @param placeId Unique identifier of the hospital
     * @return List of vaccine names; returns "No information" if retrieval fails
     */
    public List<String> getVaccinesForHospital(String placeId) {
        try {
            // URL-encode the service key
            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

            // Construct the API URL including placeId and encoded service key
            String apiUrl = "https://apis.data.go.kr/1790387/orglist3/getOrgList3?placeId=" + placeId + "&serviceKey=" + encodedKey;

            // Set HTTP headers to accept JSON responses
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCEPT, "application/json");

            // Create HTTP entity with headers and no body
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Execute GET request
            ResponseEntity<VaccineInfoResponse> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, VaccineInfoResponse.class
            );

            System.out.println("Response Body: " + response.getBody());

            // If response is OK and body is not null, return flattened vaccine list
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<VaccineInfoResponse.VaccineData> vaccineDataList = response.getBody().getData();
                return vaccineDataList.stream()
                        .flatMap(data -> data.getVaccine().stream())  // Flatten inner lists
                        .collect(Collectors.toList());
            } else {
                return Collections.singletonList("No information");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonList("No information (API error)");
        }
    }
}
