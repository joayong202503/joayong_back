package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.match.entity.Match;
import com.joayong.skillswap.domain.message.dto.request.MessageRequest;
import com.joayong.skillswap.domain.message.dto.response.MessageDetailResponse;
import com.joayong.skillswap.domain.message.dto.response.MessageResponse;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.dto.common.PageResponse;
import com.joayong.skillswap.enums.MessageType;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.*;
import com.joayong.skillswap.util.FileUploadUtil;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MessageService {

    private final MessageImageUrlRepository urlRepository;
    private final MessageRepository messageRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final MatchRepository matchRepository;

    private final FileUploadUtil fileUploadUtil;


    // 메세지 전송 서비스
    public String sendMessage(String email, MessageRequest dto, List<MultipartFile> images) {


        Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_POST)
        );

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );

        // 메세지 전송가능 여부 확인
        if (!isMessageSendable(post, user)) {
            throw new PostException(ErrorCode.ALREADY_SENT_MESSAGE);
        }
        ;

        Message message = Message.builder()
                .content(dto.getContent())
                .post(post)
                .sender(user)
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
    public List<MessageResponse> findMessages(String email, String filter, String status) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        PostStatus postStatus = null;
        MessageType messageType = MessageType.valueOf(filter);
        if (status != null) {
            postStatus = PostStatus.valueOf(status);
        }

        List<MessageResponse> messageList = new ArrayList<>();

        switch (messageType) {
            // 보낸 메일 조회
            case SEND: {
                if (status == null) {
                    messageList = messageRepository.findBySenderId(user.getId())
                            .stream().map(message -> {
                                return MessageResponse.toDto(message, true);
                            }).toList();
                    ;
                    break;
                }
                messageList = messageRepository.findBySenderIdAndMsgStatus(user.getId(), postStatus)
                        .stream().map(message -> {
                            return MessageResponse.toDto(message, true);
                        }).toList();
                ;
                break;
            }
            case RECEIVE: {
                if (status == null) {
                    messageList = messageRepository.findByPostWriter(user)
                            .stream().map(message -> {
                                return MessageResponse.toDto(message, false);
                            }).toList();
                    ;
                    break;
                }
                messageList = messageRepository.findByPostWriterAndMsgStatus(user, postStatus)
                        .stream().map(message -> {
                            return MessageResponse.toDto(message, false);
                        }).toList();
                ;
                break;
            }
            case ALL: {
                if (status == null) {

                    List<MessageResponse> receiveMessageList = new ArrayList<>(messageRepository.findByPostWriter(user)
                            .stream().map(message -> {
                                return MessageResponse.toDto(message, false);
                            }).toList());

                    List<MessageResponse> sendMessageList = messageRepository.findBySenderId(user.getId())
                            .stream().map(message -> {
                                return MessageResponse.toDto(message, true);
                            }).toList();

                    receiveMessageList.addAll(sendMessageList);

                    messageList = receiveMessageList;
                    break;
                }
                List<MessageResponse> sendMessageList = new ArrayList<>(messageRepository.findBySenderIdAndMsgStatus(user.getId(), postStatus)
                        .stream().map(message -> {
                            return MessageResponse.toDto(message, true);
                        }).toList());
                List<MessageResponse> receiveMessageList = messageRepository.findByPostWriterAndMsgStatus(user, postStatus)
                        .stream().map(message -> {
                            return MessageResponse.toDto(message, false);
                        }).toList();

                sendMessageList.addAll(receiveMessageList);

                messageList = sendMessageList;
                break;
            }
        }
        log.info("messageList:{}", messageList);

        Map<String, MessageResponse> messageMap = messageList.stream()
                // 시간순 정렬
                .sorted(Comparator.comparing(MessageResponse::getSentAt).reversed())
                // 중복제거
                .collect(Collectors.toMap(
                        MessageResponse::getMessageId,   // Key: messageId
                        Function.identity(),     // Value: Message 객체
                        (existing, replacement) -> existing // 중복 발생 시 기존 값 유지
                ));

        return new ArrayList<>(messageMap.values());
    }

    // 메세지 발송 가능 여부 확인 서비스
    public boolean canSendMessage(String postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_POST)
        );

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );

        return isMessageSendable(post, user);
    }

    // 메세지 발송 여부 확인 메서드
    public boolean isMessageSendable(Post post, User sender) {
        // 게시글의 메세지리스트
        List<Message> messageList = post.getMessageList();

        // 로그인한 유저의 메세지만 필터
        List<Message> senderMessageList = messageList.stream()
                .filter(message -> message.getSender() == sender)
                .toList();

        // 이미 메세지를 보내 수락대기중이거나 수락됬을 시엔 발송 불가
        for (Message message : senderMessageList) {
            if (message.getMsgStatus() == PostStatus.N
                || message.getMsgStatus() == PostStatus.M) {
                return false;
            }
        }
        return true;
    }

    // 메세지 수락 서비스
    public boolean acceptMessage(String messageId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_MESSAGE)
        );

        Post post = message.getPost();

        // 본인이 받은 메세지가 아닐 시 예외처리
        if (post.getWriter() != user) {
            throw new PostException(ErrorCode.NOT_MY_RECEIVED_MESSAGE);
        }

        // 메세지 상태 변경
        message.setMsgStatus(PostStatus.M);
        messageRepository.save(message);

        // 게시글의 상태도 변경
        post.setStatus(PostStatus.M);
        postRepository.save(post);

        // 수락했다면 매칭 테이블 데이터 생성
        Match matching = Match.builder()
                .client(message.getSender())
                .post(post)
                .build();

        matchRepository.save(matching);

        return true;
    }

    public boolean rejectMessage(String messageId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_MESSAGE)
        );

        Post post = message.getPost();

        // 본인이 받은 메세지가 아닐 시 예외처리
        if (post.getWriter() != user) {
            throw new PostException(ErrorCode.NOT_MY_RECEIVED_MESSAGE);
        }

        // 메세지 상태 변경
        message.setMsgStatus(PostStatus.D);
        messageRepository.save(message);

        return true;
    }

    public PageResponse<MessageResponse> findPagingMessage(String email, String filter, String status, Pageable pageable) {

        MessageType messageType = MessageType.valueOf(filter);
        PostStatus postStatus = (status != null) ? PostStatus.valueOf(status) : null;

        // 조건에 따른 전체 데이터 개수 조회
        long totalCount = messageRepository.countMessages(messageType, postStatus, email, pageable);

        if (totalCount == 0) {
            return PageResponse.<MessageResponse>builder()
                    .totalCount(0)
                    .totalPages(0)
                    .currentPage(pageable.getPageNumber() + 1) // 클라이언트에선 1페이지가 첫페이지니까 더해줌
                    .hasNext(false)
                    .hasPrevious(false)
                    .pageSize(pageable.getPageSize())
                    .data(Collections.emptyList()) // 빈 리스트 반환
                    .build();
        }

        List<Tuple> messageTupleList = messageRepository.getMessageList(email, messageType, postStatus, pageable);

        List<MessageResponse> messageResponseList
                = messageTupleList.stream().map(MessageResponse::toDtoPage).toList();

        // 페이지네이션 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageable.getPageSize());
        boolean hasNext = pageable.getPageNumber() < totalPages - 1;
        boolean hasPrevious = pageable.getPageNumber() > 0;

        // 제너릭 타입 T를 MessageResponse로 지정하고, build() 메서드를 호출하여 객체를 반환
        return PageResponse.<MessageResponse>builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(pageable.getPageNumber() + 1) // 클라이언트에선 1페이지가 첫페이지니까 더해줌
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .pageSize(pageable.getPageSize())
                .data(messageResponseList)
                .build();

    }


    // 메세지 이미지 조회 서비스
    public MessageDetailResponse getMessageUrlList(String messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_MESSAGE)
        );
        return MessageDetailResponse.toDto(message);
    }

}
