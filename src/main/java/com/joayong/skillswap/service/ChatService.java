package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.chat.entity.ChatRoom;
import com.joayong.skillswap.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createOrGetChatRoom(String user1Id, String user2Id) {
        // 순서 상관없이 중복 체크
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUser1IdAndUser2Id(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        existingRoom = chatRoomRepository.findByUser2IdAndUser1Id(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // 새로운 채팅방 생성
        ChatRoom chatRoom = new ChatRoom(user1Id, user2Id);
        return chatRoomRepository.save(chatRoom);
    }

    // 특정 유저가 참여한 채팅방 목록 조회
    public List<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByUser1IdOrUser2Id(userId, userId);
    }
}
