package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.room.entity.RtcRoom;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.RtcRoomRepository;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RtcRoomService {

    private final RtcRoomRepository rtcRoomRepository;

    public int getRtcRoomCode(String messageId, String email) {

        RtcRoom rtcRoom = rtcRoomRepository.findByIsAvailable(messageId).orElse(null);

        if (rtcRoom == null) {
            RtcRoom newRoom = rtcRoomRepository.findFirstByIsAvailableIsNull().orElseThrow(
                    () -> new PostException(ErrorCode.NO_EMPTY_ROOM)
            );

            newRoom.setIsAvailable(messageId);
            rtcRoomRepository.save(newRoom);

            return newRoom.getRoomId();
        }

        return rtcRoom.getRoomId();
    }
}
