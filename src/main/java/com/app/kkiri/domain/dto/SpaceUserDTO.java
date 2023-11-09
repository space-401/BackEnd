package com.app.kkiri.domain.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceUserDTO {
    private Long spaceId;
    private Long userId;
    private Boolean isAdmin;
    private String userNickname;
}
