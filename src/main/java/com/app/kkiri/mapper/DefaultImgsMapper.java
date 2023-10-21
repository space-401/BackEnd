package com.app.kkiri.mapper;

import com.app.kkiri.domain.vo.DefaultImgVO;
import com.app.kkiri.domain.vo.SpaceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DefaultImgsMapper {
    // 기본이미지 조회
    public DefaultImgVO selectByOrder(Long defaultImgOrder);

    // 기본 이미지 추가
    public void insert(DefaultImgVO defaultImgVO);

    // 기본 이미지 삭제
    public void delete(Long defaultImgId);

}
