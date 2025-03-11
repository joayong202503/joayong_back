package com.joayong.skillswap.serivice;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.dto.request.MessageRequest;
import com.joayong.skillswap.domain.message.dto.response.MessageResponse;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.MessageImageUrlRepository;
import com.joayong.skillswap.repository.MessageRepository;
import com.joayong.skillswap.repository.PostRepository;
import com.joayong.skillswap.repository.UserRepository;
import com.joayong.skillswap.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MessageService {

    private final MessageImageUrlRepository urlRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final FileUploadUtil fileUploadUtil;


    // 메세지 전송 서비스
    public String sendMessage(String email, MessageRequest dto, List<MultipartFile> images) {

        Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_POST)
        );

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.MEMBER_NOT_FOUND)
        );
        Message message = Message.builder()
                .content(dto.getContent())
                .post(post)
                .sender(post.getWriter())
                .build();

        messageRepository.save(message);

        // 이미지가 존재 할 경우
        if (images != null) {
            log.info("images is not null");
            List<MessageImageUrl> imageUrlList = processImages(images, dto, message);
            message.setMessageImages(imageUrlList);
            messageRepository.save(message);
        }

        return message.getId();
    }

    // 이미지 처리 메서드
    private List<MessageImageUrl> processImages(List<MultipartFile> images
            , MessageRequest dto, Message message) {
        log.debug("start process Image!!");

        List<MessageImageUrl> ImageUrlList = new ArrayList<>();
        // 이미지들을 서버(/upload 폴더)에 저장
        if (images != null && !images.isEmpty()) {
            log.debug("save process Image!!");

            int order = 1; // 이미지 순서
            for (MultipartFile image : images) {
                // 파일 서버에 저장
                String uploadedUrl = fileUploadUtil.saveFile(image);

                // 이미지들을 데이터베이스 post_images 테이블에 insert
                log.debug("success to save file at: {}", uploadedUrl);

                // 이미지들을 데이터베이스 post_images 테이블에 insert
                MessageImageUrl messageImageUrl = MessageImageUrl.builder()
                        .message(message)
                        .imageUrl(uploadedUrl)
                        .sequence(order)
                        .build();

                urlRepository.save(messageImageUrl);
                ImageUrlList.add(messageImageUrl);
            }
        }
        return ImageUrlList;
    }

    // 메세지 조회 서비스
    public List<MessageResponse> findMessages(String email, String option) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.MEMBER_NOT_FOUND)
        );


        return null;
    }
}
