package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class PostPositionDTO {
    private Double lat;
    private Double lng;

    public void create(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
