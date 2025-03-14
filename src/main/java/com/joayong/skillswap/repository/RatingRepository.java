package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating,String> {
    Optional<Rating> findByUser(User user);
}
