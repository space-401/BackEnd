package com.app.kkiri.domain.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.app.kkiri.domain.dto.response.SpaceUserRespnseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceListDTO {
    private Long spaceId;
    private String spaceTitle;
    private String imgUrl;
    private List<SpaceUserRespnseDTO> userList;

}
