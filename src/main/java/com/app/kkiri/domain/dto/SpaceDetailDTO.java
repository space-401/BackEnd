package com.app.kkiri.domain.dto;

import java.util.List;

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
    private int isAdmin;
    private int isFirst;
    private List<SpaceListDTO> userList;
    private List<TagDTO> tagList;
}
