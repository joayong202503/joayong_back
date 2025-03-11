package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findBySenderId(String senderId);

    List<Message> findByPostId(String postId);

    List<Message> findByPostIdAndMsgStatus(String postId, PostStatus msgStatus);

    List<Message> findBySenderIdAndMsgStatus(String senderId, PostStatus postStatus);

    List<Message> findByPostWriter(User writer);

}
