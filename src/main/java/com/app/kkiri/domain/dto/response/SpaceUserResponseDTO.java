package com.app.kkiri.domain.dto.response;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceUserResponseDTO {
    private Long userId;
    private String userName;
    private String imgUrl;
}
