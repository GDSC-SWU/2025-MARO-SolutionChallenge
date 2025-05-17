package me.seungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionInfo {
    private String si; // Province or metropolitan city name (e.g., Seoul, Busan)
    private String gu; // District or ward name (e.g., Gangnam-gu, Jung-gu)

}
