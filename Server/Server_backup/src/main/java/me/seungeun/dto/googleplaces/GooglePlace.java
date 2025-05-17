package me.seungeun.dto.googleplaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GooglePlace {
    private String name;

    @JsonProperty("place_id")
    private String place_id;  // Unique Google Place ID

    private String vicinity;  // Nearby address or vicinity info

    @JsonProperty("formatted_phone_number")
    private String formattedPhoneNumber;  // Phone number in formatted style

    private Geometry geometry;  // Location info (latitude, longitude)

    @JsonProperty("opening_hours")
    private OpeningHours openingHours;  // Opening hours info

    @Data
    public static class OpeningHours {
        @JsonProperty("weekday_text")
        private List<String> weekdayText;  // List of opening hours strings per weekday
    }

    // Safely get weekday opening hours text list
    public List<String> getWeekdayText() {
        return openingHours != null ? openingHours.getWeekdayText() : null;
    }

    @Data
    public static class Geometry {
        private Location location;

        @Data
        public static class Location {
            private double lat;  // Latitude
            private double lng;  // Longitude
        }
    }
}
