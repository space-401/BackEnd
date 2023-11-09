package com.app.kkiri.domain.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceUserRespnseDTO {
    private Long userId;
    private String userName;
    private String imgUrl;
}
