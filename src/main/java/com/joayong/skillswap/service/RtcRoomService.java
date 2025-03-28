package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.room.dto.RtcResponse;
import com.joayong.skillswap.domain.room.entity.RtcRoom;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.RtcRoomRepository;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RtcRoomService {

    private final RtcRoomRepository rtcRoomRepository;

    public RtcResponse getRtcRoomCode(String messageId, String email) {

        RtcRoom rtcRoom = rtcRoomRepository.findByIsAvailable(messageId).orElse(null);

        if (rtcRoom == null) {
            RtcRoom newRoom = rtcRoomRepository.findFirstByIsAvailableIsNull().orElseThrow(
                    () -> new PostException(ErrorCode.NO_EMPTY_ROOM)
            );

            newRoom.setIsAvailable(messageId);
            rtcRoomRepository.save(newRoom);

            // true면 처음 들어가는 방
            return RtcResponse.builder().roomId(newRoom.getRoomId()).isNew(true).build();
        }

        return RtcResponse.builder().roomId(rtcRoom.getRoomId()).isNew(false).build();
    }
}
