package me.seungeun.dto.publicdata;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class PublicDataApiDto {
    private int currentCount;           // Current count of data items in the response
    private List<LocationRequestDto> data;  // List of location data objects
}
