package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.MessageStatus;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.repository.custom.MessageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String>, MessageRepositoryCustom {

    List<Message> findBySenderId(String senderId);

    List<Message> findByPostWriter(User writer);

    List<Message> findByPostIdAndMsgStatus(String postId, MessageStatus msgStatus);

    List<Message> findBySenderIdAndMsgStatus(String senderId, MessageStatus msgStatus);

    List<Message> findByPostWriterAndMsgStatus(User writer,MessageStatus msgStatus);

}
