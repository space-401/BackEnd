package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

import java.util.List;

@CommonsLog
@Data
@NoArgsConstructor
public class PostFilterResponseDTO {
    private List<PostFilterDTO> postList;
    private int page;
    private int total;
    private int itemLength;
}
