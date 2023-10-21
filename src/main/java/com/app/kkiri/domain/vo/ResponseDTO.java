package com.app.kkiri.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class ResponseDTO {
    private int status;
    private String message;

    public ResponseDTO(int status, String message){
        this.status = status;
        this.message = message;
    }
}
