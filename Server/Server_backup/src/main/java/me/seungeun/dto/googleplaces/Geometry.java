package me.seungeun.dto.googleplaces;

import lombok.Data;

@Data
public class Geometry {
    private Location location;

    @Data
    public static class Location {
        private double lat;  // Latitude coordinate
        private double lng;  // Longitude coordinate
    }
}
