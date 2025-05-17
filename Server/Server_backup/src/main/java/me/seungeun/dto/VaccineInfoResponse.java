package me.seungeun.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class VaccineInfoResponse {

    @JsonProperty("data")
    private List<VaccineData> data;  // List of vaccine center data

    public List<VaccineData> getData() {
        return data;
    }

    public void setData(List<VaccineData> data) {
        this.data = data;
    }

    public static class VaccineData {

        @JsonProperty("centerName")
        private String centerName;  // Name of the vaccination center

        @JsonProperty("vaccine")
        private List<String> vaccine;  // List of vaccines available at the center

        public String getCenterName() {
            return centerName;
        }

        public void setCenterName(String centerName) {
            this.centerName = centerName;
        }

        public List<String> getVaccine() {
            return vaccine;
        }

        public void setVaccine(List<String> vaccine) {
            this.vaccine = vaccine;
        }
    }
}
