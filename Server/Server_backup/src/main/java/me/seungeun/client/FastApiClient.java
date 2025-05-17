package me.seungeun.client;

import lombok.RequiredArgsConstructor;
import me.seungeun.dto.FastApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FastApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.model.url}")
    private String fastApiUrl;

    public FastApiResponse generateResponse(String userQuestion) {
        // 1. Set HTTP request headers: Content-Type set to JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Create request body: include all required fields for FastAPI
        Map<String, String> request = Map.of(
                "user_question", userQuestion,
                "user_lang", "en"  // Mandatory language parameter required by FastAPI
        );

        // 3. Combine request body and headers into HttpEntity
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        // 4. Execute POST request (send as JSON)
        return restTemplate.postForObject(
                fastApiUrl + "/api/callscript/help",
                entity,
                FastApiResponse.class
        );
    }
}
