package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,String> {

}
