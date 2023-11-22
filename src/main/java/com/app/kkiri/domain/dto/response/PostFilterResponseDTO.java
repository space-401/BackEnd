package com.app.kkiri.domain.dto.response;

import java.util.List;

import com.app.kkiri.domain.dto.PostFilterDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@Data
@NoArgsConstructor
public class PostFilterResponseDTO {

    private List<PostFilterDTO> postList;
    private int page;
    private int total;
    private int itemLength;
}
