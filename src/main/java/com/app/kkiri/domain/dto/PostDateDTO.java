package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class PostDateDTO {
    private String startDate;
    private String endDate;

    public void create(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
