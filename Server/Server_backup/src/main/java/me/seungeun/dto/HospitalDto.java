package me.seungeun.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDto {
    private String placeId;       // Unique hospital ID
    private String name;          // Hospital name
    private String address;       // Address
    private String phone;         // Phone number
    private double lat;           // Latitude
    private double lng;           // Longitude
    private String weekday;       // Operating days/hours
    private List<String> vaccines; // List of available vaccines

    /**
     * Creates a new HospitalDto by applying translated fields over an original HospitalDto.
     *
     * @param original Original HospitalDto object
     * @param translatedTexts Map of field names (keys) to translated strings (values)
     * @return New HospitalDto with translated fields applied
     */
    public static HospitalDto from(HospitalDto original, Map<String, String> translatedTexts) {

        List<String> translatedVaccines = translatedTexts.containsKey("vaccines")
                ? List.of(translatedTexts.get("vaccines").split(",\\s*|\\n")) // Split vaccine list by comma or newline
                : original.getVaccines();

        return HospitalDto.builder()
                .placeId(original.getPlaceId())
                .name(translatedTexts.getOrDefault("name", original.getName()))
                .address(translatedTexts.getOrDefault("address", original.getAddress()))
                .phone(translatedTexts.getOrDefault("phone", original.getPhone()))
                .lat(original.getLat())
                .lng(original.getLng())
                .weekday(translatedTexts.getOrDefault("weekday", original.getWeekday()))
                .vaccines(translatedVaccines)
                .build();
    }
}
