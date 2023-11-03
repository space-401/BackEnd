package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class TagDTO {
    private Long tagId;
    private String tagTitle;
}
