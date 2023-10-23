package com.app.kkiri.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Data
@NoArgsConstructor
public class SpaceUserDTO {
    private Long spaceId;
    private Long userId;
    private Boolean isAdmin;
    private String userNickname;
}
