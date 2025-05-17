package me.seungeun.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.seungeun.dto.ChatResponseDto;
import me.seungeun.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController // Declares this class as a REST API controller
@CrossOrigin(origins = "*") // Allows cross-origin requests from any domain
@RequestMapping("/api/chat") // Base path for all endpoints in this controller
@RequiredArgsConstructor // Automatically generates constructor for required dependencies
@Slf4j // Enables logging using SLF4J
public class ChatController {

    // Dependency injection of ChatService
    private final ChatService chatService;

    /**
     * Handles POST requests with multipart/form-data to receive question and language,
     * then queries AI and returns the answer.
     *
     * @param question user question text
     * @param language requested language code (e.g. "ko", "en")
     * @return ChatResponseDto with AI answer and HTTP 200 status
     */
    @PostMapping(value = "/free", consumes = "multipart/form-data")
    public ResponseEntity<ChatResponseDto> askFreeQuestion(
            @RequestPart("question") String question,
            @RequestPart("language") String language) {

        log.info("Request received: /api/chat/free, question={}, language={}", question, language);

        log.info("ChatService.getAnswerFromAI Call started: question={}, language={}", question, language);

        // Fetch answer from AI service
        String answer = chatService.getAnswerFromAI(question, language);

        log.info("ChatService.getAnswerFromAI Call completed: answer={}", answer);

        ChatResponseDto responseDto = new ChatResponseDto(true, answer); // Create response DTO
        return new ResponseEntity<>(responseDto, HttpStatus.OK); // Return 200 OK response
    }

    /**
     * Handles POST requests with multipart/form-data for image and text,
     * queries AI with both, and returns the answer.
     *
     * @param text question text
     * @param image uploaded image file
     * @param language optional language code (default "ko")
     * @return ChatResponseDto with AI answer and HTTP 200 status
     */
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ResponseEntity<ChatResponseDto> askWithImage(
            @RequestPart("text") String text,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "language", required = false) String language) {

        if (language == null) {
            language = "ko"; // Default to Korean
        }

        log.info("Request received: /api/chat/image, text={}, language={}, image={}",
                text, language, image != null ? image.getOriginalFilename() : "none");

        // Fetch answer from AI with image and text
        String answer = chatService.getAnswerWithImage(text, image, language);

        ChatResponseDto responseDto = new ChatResponseDto(true, answer); // Create response DTO
        return new ResponseEntity<>(responseDto, HttpStatus.OK); // Return 200 OK response
    }
}
