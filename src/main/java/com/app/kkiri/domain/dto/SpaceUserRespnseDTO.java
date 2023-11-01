package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class SpaceUserRespnseDTO {
    private Long userId;
    private String userName;
    private String imgUrl;
}
