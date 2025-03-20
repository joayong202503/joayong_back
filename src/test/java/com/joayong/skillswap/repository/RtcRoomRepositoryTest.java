package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.room.entity.RtcRoom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class RtcRoomRepositoryTest {

    @Autowired
    RtcRoomRepository roomRepository;

    @Test
    @DisplayName("사용할 수 있는 가장 작은수의 방번호는 10001이다")
    void findFirstByIsAvailableFalseOrderByRoomIdTest() {
        //given

        //when
        RtcRoom rtcRoom = roomRepository.findFirstByIsAvailableFalseOrderByRoomId()
                .orElseThrow();
        //then
        Integer roomId = rtcRoom.getRoomId();
        assertEquals(10001,roomId);
    }
}