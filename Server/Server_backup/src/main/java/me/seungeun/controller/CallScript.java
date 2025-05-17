package me.seungeun.controller;

import lombok.RequiredArgsConstructor;
import me.seungeun.client.FastApiClient;
import me.seungeun.dto.FastApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") // Base URL path for all endpoints in this controller
public class CallScript {

    // Used to send requests to the FastAPI backend
    private final FastApiClient fastApiClient;

    // Maps POST requests sent to /api/ask
    @PostMapping("/callscript/help")

    public ResponseEntity<FastApiResponse> ask(@RequestBody Map<String, String> payload) {

        // Extracts the "question" field from the payload
        String userQuestion = payload.get("question");

        // Sends the extracted question to the FastAPI server and receives a structured response
        FastApiResponse response = fastApiClient.generateResponse(userQuestion);

        System.out.println("Answers size: " + (response.getAnswers() == null ? 0 : response.getAnswers().size()));

        return ResponseEntity.ok(response);
    }
}
