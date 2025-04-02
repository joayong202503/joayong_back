package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.enums.MessageStatus;
import com.joayong.skillswap.enums.MessageType;
import com.joayong.skillswap.enums.PostStatus;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepositoryCustom {

    // 조건별 갯수 구하는 쿼리
    long countMessages(MessageType messageType, MessageStatus messageStatus, String email, Pageable pageable);

    public List<Tuple> getMessageList(String email, MessageType messageType, MessageStatus messageStatus, Pageable pageable) ;
}
