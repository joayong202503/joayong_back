package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.match.entity.Match;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.MessageStatus;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MessageImageUrlRepository imageUrlRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MatchRepository matchRepository;

    @Test
    @DisplayName("메세지를 옵션에 따라 조회가능하다")
    void findBySenderIdAndMsgStatusTest() {
        //given
        User sender = userRepository.findByEmail("p@p.com").orElseThrow();
        //when
//        List<Message> list = messageRepository.findBySenderIdAndMsgStatus("072c17ea-0a46-40df-b9e8-2aa41afb096e", PostStatus.N);
        List<Message> list = messageRepository.findBySenderIdAndMsgStatus(sender.getId(), MessageStatus.N);

        //then
        System.out.println("list = " + list);

        list.forEach((message -> {
            List<MessageImageUrl> imageUrlList = imageUrlRepository.findByMessage(message);
            System.out.println("imageUrlList = " + imageUrlList);
        }));
    }

    @Test
    @DisplayName("\"p@pcom\"가 보낸 메세지를 조회한다")
    void findBySenderTest() {
        //given
        User sender = userRepository.findByEmail("p@p.com").orElseThrow();

        //when
        List<Message> list = messageRepository.findBySenderId(sender.getId());

        //then
        System.out.println("list = " + list);

        list.forEach((message -> {
            List<MessageImageUrl> imageUrlList = imageUrlRepository.findByMessage(message);
            System.out.println("imageUrlList = " + imageUrlList);
        }));
    }

    @Test
    @DisplayName("\"p@p.com\"가 받은 메세지를 조회한다")
    void findByWriterTest() {
        //given
        User receiver = userRepository.findByEmail("p@p.com").orElseThrow();

        //when
        List<Post> postList = postRepository.findByWriter(receiver);

        Map<String, List<Message>> hashMap = new HashMap<>();

        postList.forEach(post -> {
            List<Message> messageList = messageRepository.findByPostWriter(receiver);
            hashMap.put(post.getId(), messageList);
        });

        //then

        System.out.println("hashMap = " + hashMap);
    }

    @Test
    @DisplayName("\"p@p.com\"가 받은 메세지를 조회한다")
    void findByPostWriterAndMsgStatusTest() {
        //given
        User receiver = userRepository.findByEmail("p@p.com").orElseThrow();

        //when
        List<Message> byPostWriter = messageRepository.findByPostWriterAndMsgStatus(receiver, MessageStatus.C);

        //then

        System.out.println("byPostWriter = " + byPostWriter);
    }

    @Test
    @DisplayName("메세지 수락시 메세지의 상태값과 게시글의 상태값이 M으로 변한다")
    void acceptMessageTest() {
        //given
        String messageId = "f5d26fd8-2916-43e5-a29b-1b2fb7795c5d";
        String email = "q@q.com";

        //when
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
        message.setMsgStatus(MessageStatus.M);
        messageRepository.save(message);

        // 게시글의 상태도 변경
        post.setStatus(PostStatus.M);
        postRepository.save(post);

        Match match = Match.builder().client(user).post(post).build();
        matchRepository.save(match);

        //then
        Message message2 = messageRepository.findById(messageId).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_MESSAGE)
        );

        Post post2 = message.getPost();

        MessageStatus msgStatus = message2.getMsgStatus();
        PostStatus status = post2.getStatus();

        int size = post2.getMatchList().size();

        System.out.println("msgStatus = " + msgStatus);
        System.out.println("status = " + status);
        System.out.println("matchList = " + size);
    }


}