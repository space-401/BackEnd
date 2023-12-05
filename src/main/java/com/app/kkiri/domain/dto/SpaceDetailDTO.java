package com.app.kkiri.domain.dto;

import java.util.List;

import com.app.kkiri.domain.dto.response.SpaceUserResponseDTO;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceDetailDTO {
    private String spaceTitle;
    private String spaceDescription;
    private String imgUrl;
    private String spacePw;
    private String spaceCode;
    private int isAdmin;
    private int isFirst;
    private List<SpaceUserResponseDTO> userList;
    private List<TagDTO> tagList;
}
