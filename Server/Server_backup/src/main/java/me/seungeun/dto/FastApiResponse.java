package me.seungeun.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FastApiResponse {
    private String user_question;          // User's original question
    private String question_ko;            // Question translated to Korean
    private String question_ko_audio;      // Korean audio version of the question
    private List<AnswerData> answers;      // List of answer data objects

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnswerData {
        private String answer_native;           // Answer in the user's native language
        private String answer_native_audio;     // Native language answer audio
        private String answer_ko;               // Answer translated to Korean
        private String answer_ko_audio;         // Korean audio version of the answer
        private List<String> keywords_ko;       // Keywords in Korean
        private List<String> keywords_native;   // Keywords in native language
        private List<String> keywords_ko_audio; // Audio data of Korean keywords
        private List<String> keywords_native_audio; // Audio data of native keywords
    }
}
