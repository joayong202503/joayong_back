package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.room.entity.RtcRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RtcRoomRepository extends JpaRepository<RtcRoom,Integer> {

    // 사용 가능한 방 중 가장 작은 번호 찾기
    Optional<RtcRoom> findFirstByIsAvailableIsNull();

    // 메세지에 할당 된 방 찾기 (이미 한명이 들어와있는)
    Optional<RtcRoom> findByIsAvailable(String isAvailable);

}
