package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.category.entity.CategoryRegion;
import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.post.dto.request.PostCreateRequest;
import com.joayong.skillswap.domain.post.dto.request.UpdatePostRequest;
import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.exception.UserException;
import com.joayong.skillswap.repository.*;
import com.joayong.skillswap.util.FileUploadUtil;
import com.p6spy.engine.logging.Category;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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


        if (images != null && !images.isEmpty()) {
            AtomicInteger index = new AtomicInteger(0); // AtomicInteger 초기화

            images.stream()
                    .filter(image -> image != null && !image.isEmpty()) // 추가적인 null 체크
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
    }

    //게시글 수정
    public void updatePost(String email, UpdatePostRequest request, List<MultipartFile> images) {
        User founduser = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.USER_NOT_FOUND));
        CategoryTalent giveTalent = categoryTalentRepository.findById(request.getTalentGId())
                .orElseThrow(() -> new IllegalArgumentException("해당 줄 재능을 찾을 수 없습니다."));
        CategoryTalent takeTalent = categoryTalentRepository.findById(request.getTalentTId())
                .orElseThrow(() -> new IllegalArgumentException("해당 받을 재능을 찾을 수 없습니다."));
        CategoryRegion region = categoryRegionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지역을 찾을 수 없습니다."));

        postRepository.updatePost(founduser.getId(),request);

        AtomicInteger index = new AtomicInteger(0); // AtomicInteger 초기화

        PostResponse post = postRepository.findPostById(request.getPostId());
        Optional<PostItem> postItem = postItemRepository.findById(post.getPostItemId());
        images.stream()
                .filter(image -> image != null && !image.isEmpty())
                .forEach(image -> {
                    String uploadPath = fileUploadUtil.saveFile(image);
                    PostImageUrl url = PostImageUrl.builder()
                            .postItem(postItem.get())
                            .sequence(index.getAndIncrement()) // 현재 인덱스를 가져오고 1 증가
                            .imageUrl(uploadPath)
                            .build();
                    postImageUrlRepository.save(url);
                });
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public Map<String,Object> findPosts(Pageable pageable) {
        Slice<PostResponse> posts = postRepository.findPosts(pageable);
        if(posts.isEmpty()||posts==null){
            throw new PostException(ErrorCode.NOT_FOUND_POST);
        }

        return Map.of(
                "hasNext", posts.hasNext()
                , "postList", posts
        );
    }

    public PostResponse findPostById(String id) {
        return Optional.ofNullable(postRepository.findPostById(id))
                .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_POST));
    }

    public void deletePost(String postId,String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        if(foundUser.isEmpty()){
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }
        long deleted = postRepository.deletePost(postId,foundUser.get().getId());
        if(deleted==0){
            throw new RuntimeException("삭제요청실패");
        }
    }

    public List<PostResponse> findMyPosts(String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        if(foundUser.isEmpty()){
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }
        return postRepository.findMyPosts(foundUser.get().getId());
    }

    //유저 이름으로 게시글 불러오기
    public List<PostResponse> findUserPosts(String name) {
        userRepository.findByName(name).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        List<PostResponse> foundPosts = postRepository.findUserPosts(name);
        if(foundPosts.isEmpty() || foundPosts==null){
            throw new PostException(ErrorCode.NOT_FOUND_POST);
        }
        return foundPosts;
    }

    public void viewCount(String postId,String email) {
        log.info("postId : "+postId+", email : "+email);
        if(email.equals("anonymousUser")){
            postRepository.viewCount(postId);
            log.info("뷰카운트 체크");
            return;
        }
        User founduser = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.USER_NOT_FOUND));
        postRepository.viewCountWithId(postId, founduser.getId());
    }


    public int getOnlyViewCount(String postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_POST)
        );
        return post.getViewCount();
    }

    public Map<String,Object> searchByOption(String keyword,Pageable pageable) {
        Slice<PostResponse> posts = postRepository.searchPosts(keyword,pageable);

        if(posts.isEmpty()) throw new PostException(ErrorCode.SEARCH_NOT_FOUND);
        return Map.of(
                "hasNext", posts.hasNext()
                , "postList", posts
        );
    }
}
