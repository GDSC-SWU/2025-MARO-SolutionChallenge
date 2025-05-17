package me.seungeun.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
@Valid
@NonNull
@NotNull
public class PlaceIdRequestDto {
    private String placeId;   // Google Place ID
    private String language;  // Target language for localization or translation
}
