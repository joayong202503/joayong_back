package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, String> {
}
