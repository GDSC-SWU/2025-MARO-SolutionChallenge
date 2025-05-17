package me.seungeun.dto;

import lombok.Data;

@Data
public class ChatResponseDto {
    private boolean success;  // Indicates whether the chat response was successful
    private String response;  // The actual response message from the chat service

    public ChatResponseDto(boolean success, String response){
        this.success = success;
        this.response = response;
    }
}
