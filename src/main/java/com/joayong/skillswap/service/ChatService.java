package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.chat.dto.response.ChatResponse;
import com.joayong.skillswap.domain.chat.entity.ChatMessage;
import com.joayong.skillswap.domain.chat.entity.ChatRoom;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.UserException;
import com.joayong.skillswap.repository.ChatMessageRepository;
import com.joayong.skillswap.repository.ChatRoomRepository;
import com.joayong.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatRoom createOrGetChatRoom(String user1Name, String user2Name) {
        User user1 = userRepository.findByName(user1Name).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        User user2 = userRepository.findByName(user2Name).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        // 순서 상관없이 중복 체크
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUser1IdAndUser2Id(user1.getId(), user2.getId());
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        existingRoom = chatRoomRepository.findByUser2IdAndUser1Id(user1.getId(), user2.getId());
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // 새로운 채팅방 생성
        ChatRoom chatRoom = new ChatRoom(user1.getId(), user2.getId());
        log.info("새로 생성된 채팅방 : "+chatRoom);
        return chatRoomRepository.save(chatRoom);
    }

    // 특정 유저가 참여한 채팅방 목록 조회
    public List<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    public List<ChatResponse> getChatHistory(Long roomId) {
        List<ChatMessage> chatList = chatMessageRepository.findByChatRoomId(roomId);
        return chatList.stream()
                .map(chat->
                    ChatResponse.of(chat)
                ).collect(Collectors.toList());
    }
}
