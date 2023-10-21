package com.app.kkiri.service;

import com.app.kkiri.domain.vo.*;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.repository.DefaultImgsDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultImgService {
    private final DefaultImgsDAO defaultImgsDAO;

    public DefaultImgVO defaultImgData(Long defaultImgOrder){
        return defaultImgsDAO.findByOrder(defaultImgOrder);
    };

    @Transactional(rollbackFor = Exception.class)
    public void register(DefaultImgVO defaultImgVO){
        try{
            defaultImgsDAO.register(defaultImgVO);
        } catch (Exception e) {
            throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }

    public void remove(Long defaultImgId){
        try{
            defaultImgsDAO.remove(defaultImgId);
        } catch (Exception e) {
            throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }
}
