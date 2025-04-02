package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.repository.custom.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,String>, PostRepositoryCustom {
    List<Post> findByWriter(User writer);

}
