package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.user.dto.response.UserProfileResponse;
import com.joayong.skillswap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryCustom{
    // 프로필 이미지 업데이트
    void updateProfileImage(String imageUrl, String username);
    long updateName(String email, String newName);

    UserProfileResponse getUserProfile(String id);
}
