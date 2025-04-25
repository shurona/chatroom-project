package com.shurona.chat.mytalk.friend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FriendServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Test
    public void 쓰고_읽기_테스트() {

        //given
        String username = "username";
        String password = "password";
        String description = "description";
        String phoneNumber = "010-202-2020";

        Long userId = userService.saveUser(username, password, description, phoneNumber);

        User userA = userService.findUserById(userId);

        String username2 = "username2";
        String password2 = "password2";
        String description2 = "description2";
        String phoneNumber2 = "010-202-2022";


        Long userId2 = userService.saveUser(username2, password2, description2, phoneNumber2);
        User userB = userService.findUserById(userId2);



        // when
        List<Friend> friendListBeforeSave = friendService.findAcceptedFriendListByUser(userB);
        Friend friend = friendService.saveFriend(userA, userB);

        // 이때에는 request 이어야 한다.
        assertThat(friend.getRequest()).isEqualTo(FriendRequest.REQUESTED);

        // 친구 수락
        friendService.changeStatusById(friend.getId(), FriendRequest.ACCEPTED);
        // 친구 목록 불러온다.
        List<Friend> friendListAfterSave = friendService.findAcceptedFriendListByUser(userB);

        //then
        assertThat(friendListBeforeSave.size()).isEqualTo(0);
        assertThat(friendListAfterSave.size()).isEqualTo(1);
        assertThat(friend.getFriend().getId()).isEqualTo(userA.getId());
        assertThat(friend.getRequest()).isEqualTo(FriendRequest.ACCEPTED);
    }

    @Test
    public void 이미_친구_인_경우() {

        //given
        String username = "username";
        String password = "password";
        String description = "description";
        String phoneNumber = "010-202-2020";

        Long userId = userService.saveUser(username, password, description, phoneNumber);

        String username2 = "username2";
        String password2 = "password2";
        String description2 = "description2";
        String phoneNumber2 = "010-202-2022";

        Long userId2 = userService.saveUser(username2, password2, description2, phoneNumber2);

        User userA = userService.findUserById(userId);
        User userB = userService.findUserById(userId2);

        // when
        Friend friend = friendService.saveFriend(userA, userB);
        // 친구 수락
        friendService.changeStatusById(friend.getId(), FriendRequest.ACCEPTED);
        Friend reverseFriend = friendService.saveFriend(userB, userA);

        //then
        // 이미 수락한 경우 반대면 바로 수락이 되어야 한다.
        assertThat(reverseFriend.getRequest()).isEqualTo(FriendRequest.ACCEPTED);
    }
}