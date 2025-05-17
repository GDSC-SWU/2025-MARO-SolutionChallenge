package me.seungeun.dto.googleplaces;

import lombok.Data;

@Data
public class GooglePlaceDetailResponse {
    private GooglePlace result;
    private String status;
}
