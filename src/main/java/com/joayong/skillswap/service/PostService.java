package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.category.entity.CategoryRegion;
import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.post.dto.request.PostCreateRequest;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.UserException;
import com.joayong.skillswap.repository.*;
import com.joayong.skillswap.repository.custom.CategoryRegionRepositoryCustom;
import com.joayong.skillswap.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final UserRepository userRepository;
    private final CategoryTalentRepository categoryTalentRepository;
    private final CategoryRegionRepository categoryRegionRepository;
    private final PostRepository postRepository;
    private final PostItemRepository postItemRepository;
    private final PostImageUrlRepository postImageUrlRepository;

    private final FileUploadUtil fileUploadUtil;

    public void createPost(String email, PostCreateRequest request, List<MultipartFile> images){
        User founduser = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.USER_NOT_FOUND));

        CategoryTalent giveTalent = categoryTalentRepository.findById(request.getTalentGId())
                .orElseThrow(() -> new IllegalArgumentException("해당 줄 재능을 찾을 수 없습니다."));
        CategoryTalent takeTalent = categoryTalentRepository.findById(request.getTalentTId())
                .orElseThrow(() -> new IllegalArgumentException("해당 받을 재능을 찾을 수 없습니다."));
        CategoryRegion region = categoryRegionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지역을 찾을 수 없습니다."));

        Post post = Post.builder()
                .writer(founduser)
                .build();
        postRepository.save(post);

        PostItem postItem = PostItem.builder()
                .post(post)
                .title(request.getTitle())
                .content(request.getContent())
                .talentGId(giveTalent)
                .talentTId(takeTalent)
                .regionId(region)
                .build();
        PostItem savedPostItem = postItemRepository.save(postItem);

        AtomicInteger index = new AtomicInteger(0); // AtomicInteger 초기화

        images.stream()
                .filter(image -> image != null && !image.isEmpty())
                .forEach(image -> {
                    String uploadPath = fileUploadUtil.saveFile(image);
                    PostImageUrl url = PostImageUrl.builder()
                            .postItem(savedPostItem)
                            .sequence(index.getAndIncrement()) // 현재 인덱스를 가져오고 1 증가
                            .imageUrl(uploadPath)
                            .build();
                    postImageUrlRepository.save(url);
                });
    }

    public void findAllposts() {
        postRepository.getAllPosts();
    }
}
