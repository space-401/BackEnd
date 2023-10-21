package com.app.kkiri.repository;

import com.app.kkiri.domain.vo.DefaultImgVO;
import com.app.kkiri.mapper.DefaultImgsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultImgsDAO {
    private final DefaultImgsMapper defaultImgsMapper;
    // 기본이미지 조회
    public DefaultImgVO findByOrder(Long defaultImgOrder){ return defaultImgsMapper.selectByOrder(defaultImgOrder); };

    // 기본 이미지 추가
    public void register(DefaultImgVO defaultImgVO){ defaultImgsMapper.insert(defaultImgVO); };

    // 기본 이미지 삭제
    public void remove(Long defaultImgId){ defaultImgsMapper.delete(defaultImgId); };

}
