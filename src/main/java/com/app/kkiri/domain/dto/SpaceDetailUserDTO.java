package com.app.kkiri.domain.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceDetailUserDTO {
    private Long userId;
    private String userNickname;
    private String profileImgPath;
}
