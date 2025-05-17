package me.seungeun.dto.googleplaces;

import lombok.Data;

import java.util.List;

@Data
public class GooglePlacesResponse {
    private List<GooglePlace> results;  // List of GooglePlace results returned from API
    private String status;              // API response status code (e.g., "OK", "ZERO_RESULTS")
}
