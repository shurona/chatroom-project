package com.shurona.chat.mytalk.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.infrastructure.jpa.FriendJpaRepository;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.infrastructure.jpa.UserJpaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
public class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private FriendJpaRepository friendJpaRepository;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;


    @BeforeEach
    void setUp() {

        user1 = User.createUser("hello2025", "hello2025", "2025년을 위한 계정", "010-2222-2222");
        user2 = User.createUser("coolguy", "coolguy", "쿨한 사람입니다", "010-3333-3333");
        user3 = User.createUser("sunnyday", "sunnyday", "맑은 날씨를 좋아해요", "010-1234-1234");
        user4 = User.createUser("testuser2", "testuser2", "코딩하는 고양이", "010-5555-5555");
        user5 = User.createUser("testuser1", "testuser1", "첫 번째 테스트 계정", "010-1111-1111");

        user5.settingPassword("pwd");
        user1.settingPassword("pwd");
        user2.settingPassword("pwd");
        user3.settingPassword("pwd");
        user4.settingPassword("pwd");

        userJpaRepository.saveAll(List.of(user5, user1, user2, user3, user4));
    }

    @Test
    public void 유저_검색_테스트() {

        Page<User> nickNameContaining = userJpaRepository.findByNickNameContaining("test",
            PageRequest.of(0, 10));
        Page<User> nickNameContainingNone = userJpaRepository.findByNickNameContaining("qwer",
            PageRequest.of(0, 10));

        assertThat(nickNameContaining.toList().size()).isEqualTo(2);
        assertThat(nickNameContainingNone.toList().size()).isEqualTo(0);

    }

    @Test
    public void 친구_아닌_유저_검색() {

        // given
        User userInfo = User.createUser("iron", "iron", "변경되었습니다.", "010-6666-6666");
        userInfo.settingPassword("pwd");

        // 저장
        userJpaRepository.save(userInfo);
        Friend friend = Friend.acceptMutualFriend(userInfo, user5);
        friendJpaRepository.save(friend);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<User> userList = userJpaRepository.findNonFriendsByUserIdAndNicknameContaining(
            userInfo.getId(), "test", pageRequest);
        Page<User> userListTwo = userJpaRepository.findNonFriendsByUserIdAndNicknameContaining(
            user3.getId(), "test", pageRequest);
        Page<User> userListNotMe = userJpaRepository.findNonFriendsByUserIdAndNicknameContaining(
            user5.getId(), "test", pageRequest);

        for (User user : userListTwo) {
            System.out.println(user.getNickName());
        }

        // then
        assertThat(userList.toList().size()).isEqualTo(1);
        assertThat(userListTwo.toList().size()).isEqualTo(2);
        assertThat(userListNotMe.toList().size()).isEqualTo(1);
    }
}