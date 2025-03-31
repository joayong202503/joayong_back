package com.joayong.skillswap.enums;

public enum MessageStatus {
    N, // 전송상태
    M, // 수락상태
    C, // 최종 완료상태 (리뷰 둘다 완료)
    D, // 거절 상태
    R, // 아직 리뷰 아무도 안단 상태
    RS, // 메세지 보낸사람이 리뷰쓴 상태
    RW, // 글쓴 사람이 리뷰쓴 상태
}
