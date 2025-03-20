package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.room.entity.RtcRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RtcRoomRepository extends JpaRepository<RtcRoom,Integer> {

    // 사용 가능한 방 중 가장 작은 번호 찾기
    Optional<RtcRoom> findFirstByIsAvailableFalseOrderByRoomId();
}
