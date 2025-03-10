package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Long>, UserRepositoryCustom {
    // 중복 체크용 조회 메서드
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

}
