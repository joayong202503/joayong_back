package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageImageUrlRepository extends JpaRepository<MessageImageUrl,String> {
    public List<MessageImageUrl> findByMessage(Message message);

}
