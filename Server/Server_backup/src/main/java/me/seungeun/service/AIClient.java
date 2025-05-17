package me.seungeun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class AIClient {

    @Value("${fastapi.model.url}")
    private String fastapiModelUrl;

    public boolean sendToAI(String vaccine_ko, String vaccine_en, String period, MultipartFile image) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("vaccine_ko", vaccine_ko);
        multipartBody.add("vaccine_en", vaccine_en);
        multipartBody.add("period", period);

        Resource imageResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
        multipartBody.add("image", imageResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartBody, headers);

        ResponseEntity<HashMap> response = restTemplate.postForEntity(fastapiModelUrl + "/verify_vaccine", requestEntity, HashMap.class);

        log.info("Sending to AI: vaccine_ko={}, vaccine_en={}, period={}", vaccine_ko, vaccine_en, period);
        log.info("FastAPI URL: {}", fastapiModelUrl + "/verify_vaccine");
        log.info("AI response: {}", response.getBody());

        if (response.getBody() != null && response.getBody().containsKey("result")) {
            return Boolean.parseBoolean(response.getBody().get("result").toString());
        }
        return false;
    }
}
