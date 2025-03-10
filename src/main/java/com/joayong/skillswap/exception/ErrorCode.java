package com.joayong.skillswap.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// API에서 나오는 에러상황들을 상수로 표현
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 알 수 없는 서버오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류입니다. 점검 후 조치하겠습니다."),

    // File 관련 오류
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),
    NOT_FOUND_IMAGE_FILE(HttpStatus.NOT_FOUND, "조회되지 않는 지출내역입니다."),

    // 회원 관련 에러
    INVALID_SIGNUP_DATA(HttpStatus.BAD_REQUEST, "잘못된 회원가입 데이터입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
//    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 사용자 이름입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    // 인증 관련
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다."),

    // 방 관련 에러
    NOT_FOUND_CODE(HttpStatus.NOT_FOUND, "조회되지 않는 코드입니다."),
    INVALID_TYPE_NAME(HttpStatus.BAD_REQUEST, "잘못된 타입 이름입니다."),

    // 포스트관련 에러
    INVALID_START_END_TIME(HttpStatus.BAD_REQUEST, "시작 시간이 종료 시간보다 늦을 수 없습니다."),

    // 일정관련
    NOT_FOUND_SCHEDULE(HttpStatus.NOT_FOUND, "조회되지 않는 일정입니다."),

    // 지출관련
    NOT_FOUND_EXPENSE(HttpStatus.NOT_FOUND, "조회되지 않는 지출내역입니다."),

    // 체크리스트 관련
    NOT_FOUND_CHECKLIST(HttpStatus.NOT_FOUND, "조회되지 않는 체크리스트입니다."),
    NOT_FOUND_CHECKLIST_CATEGORY(HttpStatus.NOT_FOUND, "조회되지 않는 체크리스트의 카테고리입니다."),

    // 등록실패 오류
    FAIL_TO_RESISTER(HttpStatus.BAD_REQUEST,"등록에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String message;


}