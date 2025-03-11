package com.joayong.skillswap.serivice;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.repository.MessageImageUrlRepository;
import com.joayong.skillswap.repository.MessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Test
    @DisplayName("메세지를 옵션에 따라 조회가능하다")
    void findBySenderIdAndMsgStatusTest() {
        //given

        //when
        List<Message> list = messageRepository.findBySenderIdAndMsgStatus("072c17ea-0a46-40df-b9e8-2aa41afb096e", PostStatus.N);

        //then
        System.out.println("list = " + list);

        list.forEach((message -> {
            List<MessageImageUrl> imageUrlList = imageUrlRepository.findByMessage(message);
            System.out.println("imageUrlList = " + imageUrlList);
        }));
    }
}