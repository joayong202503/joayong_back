package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,String> {
    public List<Message> findBySenderIdAndMsgStatus(String senderId, PostStatus msgStatus);

}
