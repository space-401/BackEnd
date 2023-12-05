package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class PostFilterValueDTO {
    private Long spaceId;
    private Long page;
    private List<Long> userId;
    private List<Long> tagId;
    private String keyword;
    private String startDate;
    private String endDate;
}
