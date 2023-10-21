package com.app.kkiri.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class DefaultImgVO {
    private String defaultImgRegisterDate;
    private String defaultImgName;
    private String defaultImgPath;
    private String defaultImgUuid;
    private Long defaultImgSize;
    private Long defaultImgOrder;
}
