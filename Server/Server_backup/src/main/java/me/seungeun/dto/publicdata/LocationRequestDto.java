package me.seungeun.dto.publicdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@Valid
@NonNull
@NotNull
public class LocationRequestDto {

    // Latitude coordinate
    @JsonProperty("lat")
    private double lat;

    // Longitude coordinate
    @JsonProperty("lng")
    private double lng;

    // Language parameter for localization (e.g. "ko", "en")
    @JsonProperty("language")
    private String language;

    // Constructor
    public LocationRequestDto(double lat, double lng, String language){
        this.lat = lat;
        this.lng = lng;
        this.language = language;
    }
}
