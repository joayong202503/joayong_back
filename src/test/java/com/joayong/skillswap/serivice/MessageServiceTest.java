package com.joayong.skillswap.serivice;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.repository.MessageImageUrlRepository;
import com.joayong.skillswap.repository.MessageRepository;
import com.joayong.skillswap.repository.PostRepository;
import com.joayong.skillswap.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("메세지를 옵션에 따라 조회가능하다")
    void findBySenderIdAndMsgStatusTest() {
        //given
        User sender = userRepository.findByEmail("p@p.com").orElseThrow();
        //when
//        List<Message> list = messageRepository.findBySenderIdAndMsgStatus("072c17ea-0a46-40df-b9e8-2aa41afb096e", PostStatus.N);
        List<Message> list = messageRepository.findBySenderIdAndMsgStatus(sender.getId(), PostStatus.N);

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

        Map<String,List<Message>> hashMap= new HashMap<>();

        postList.forEach(post -> {
            List<Message> messageList = messageRepository.findByPostId(post.getId());
            hashMap.put(post.getId(),messageList);
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
        List<Message> byPostWriter = messageRepository.findByPostWriterAndMsgStatus(receiver,PostStatus.C);

        //then

        System.out.println("byPostWriter = " + byPostWriter);
    }
}