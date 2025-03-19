package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.enums.MessageType;
import com.joayong.skillswap.enums.PostStatus;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepositoryCustom {

    public List<Tuple> getMessageList(String email, MessageType messageType, PostStatus postStatus, Pageable pageable) ;
}
