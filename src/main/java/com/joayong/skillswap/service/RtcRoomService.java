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
            newRoom.setCount(newRoom.getCount() + 1);
            rtcRoomRepository.save(newRoom);

            // true면 처음 들어가는 방
            return RtcResponse.builder().roomId(newRoom.getRoomId()).isNew(true).build();
        }
        rtcRoom.setCount(rtcRoom.getCount() + 1);
        rtcRoomRepository.save(rtcRoom);
        return RtcResponse.builder().roomId(rtcRoom.getRoomId()).isNew(false).build();
    }

    public int exitRtcRoomCode(int roomId, String email) {

        RtcRoom rtcRoom = rtcRoomRepository.findById(roomId).orElse(null);

        if (rtcRoom == null) {
            throw new PostException(ErrorCode.NOT_FOUND_ROOM);
        }
        if (rtcRoom.getCount() == 1) {
            rtcRoom.setIsAvailable(null);
            rtcRoom.setCount(rtcRoom.getCount() - 1);
            rtcRoomRepository.save(rtcRoom);
        } else if (rtcRoom.getCount() == 2) {
            rtcRoom.setCount(rtcRoom.getCount() - 1);
            rtcRoomRepository.save(rtcRoom);
        }

        return rtcRoom.getCount();
    }
}
