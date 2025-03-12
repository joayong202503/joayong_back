package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.category.entity.CategoryRegion;
import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import com.joayong.skillswap.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageImageUrlRepository messageImageUrlRepository;

    @Autowired
    CategoryRegionRepository categoryRegionRepository;

    @Autowired
    CategoryTalentRepository categoryTalentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostItemRepository postItemRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeInsert() {

        System.out.println("\n======================================\n");

        User user1 = User.builder().name("aaa1").email("aaa1@aaa.com").password("aaa1111!").build();
        User user2 = User.builder().name("aaa2").email("aaa2@aaa.com").password("aaa1111!").build();

        userRepository.save(user1);
        userRepository.save(user2);

        CategoryTalent talent1 = categoryTalentRepository.findById(21L).orElseThrow();
        CategoryTalent talent2 = categoryTalentRepository.findById(31L).orElseThrow();

        CategoryRegion region = categoryRegionRepository.findById(34L).orElseThrow();

        Post post = Post.builder().writer(user1).build();

        postRepository.save(post);

        PostItem postItem
                = PostItem.builder()
                .post(post)
                .title("제목입니다.")
                .content("내용입니다.")
                .regionId(region)
                .talentGId(talent1)
                .talentTId(talent2)
                .build();
        postItemRepository.save(postItem);
        post.setPostItemList(List.of(postItem));
        postRepository.save(post);
    }

    @Test
    @DisplayName("")
    void name() {
        //given
        CategoryTalent talent1 = categoryTalentRepository.findById(21L).orElseThrow();
        CategoryTalent talent2 = categoryTalentRepository.findById(31L).orElseThrow();

        CategoryRegion region = categoryRegionRepository.findById(34L).orElseThrow();

        User user1 = userRepository.findByEmail("aaa1@aaa.com").orElseThrow();
        User user2 = userRepository.findByEmail("aaa2@aaa.com").orElseThrow();

        System.out.println("user1 = " + user1.getEmail());

        Post post = postRepository.findAll().get(0);

        //when

        Message message = Message.builder()
                .content("메세지 내용입니다.")
                .post(post)
                .sender(user2)
                .build();

        messageRepository.save(message);

        MessageImageUrl imageUrl = MessageImageUrl.builder()
                .message(message)
                .imageUrl("aaa")
                .sequence(1)
                .build();
        MessageImageUrl imageUrl1 = MessageImageUrl.builder()
                .message(message)
                .imageUrl("bbb")
                .sequence(2)
                .build();
        messageImageUrlRepository.save(imageUrl);
        messageImageUrlRepository.save(imageUrl1);

        List<MessageImageUrl> imageUrlList = messageImageUrlRepository.findAll();

        message.setMessageImages(imageUrlList);

        messageRepository.save(message);

        //then
        List<Message> messageList = messageRepository.findAll();
        System.out.println("messageList = " + messageList);

    }
}