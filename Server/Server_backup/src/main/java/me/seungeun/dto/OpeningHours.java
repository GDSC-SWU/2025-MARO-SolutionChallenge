package me.seungeun.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpeningHours {
    private List<String> weekday_text;  // List of opening hours text for each weekday
}
