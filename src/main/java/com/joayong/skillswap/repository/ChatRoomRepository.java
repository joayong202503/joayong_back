package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Optional<ChatRoom> findByUser1IdAndUser2Id(String user1Id, String user2Id);
    Optional<ChatRoom> findByUser2IdAndUser1Id(String user1Id, String user2Id);

    List<ChatRoom> findByUser1IdOrUser2Id(String userId, String userId1);
}
