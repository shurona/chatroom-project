package com.shurona.chat.mytalk.friend.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.infrastructure.jpa.FriendJpaRepository;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class FriendJpaRepositoryTest {

    @Autowired
    private FriendJpaRepository friendJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User mainUser;
    private User friend1;
    private User friend2;
    private User nonFriend;
    private List<Friend> savedFriends;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 초기화
        mainUser = createUser("main_id", "메인유저", "메인 유저입니다", "010-1111-1111");
        friend1 = createUser("friend1_id", "친구1", "친구1입니다", "010-2222-2222");
        friend2 = createUser("friend2_id", "친구2", "친구2입니다", "010-3333-3333");
        nonFriend = createUser("non_id", "비친구", "친구가 아닙니다", "010-4444-4444");

        // 친구 관계 설정
        Friend relation1 = Friend.newFriend(mainUser, friend1);

        Friend relation2 = Friend.newFriend(mainUser, friend2);
        relation2.acceptFriendRequest();

        // 친구 관계 저장
        savedFriends = new ArrayList<>();
        savedFriends.add(entityManager.persistAndFlush(relation1));
        savedFriends.add(entityManager.persistAndFlush(relation2));

    }

    private User createUser(String loginId, String nickName, String description,
        String phoneNumber) {
        User user = User.createUser(loginId, nickName, description, phoneNumber);
        user.settingPassword("password123"); // 테스트용 비밀번호 설정
        return entityManager.persistAndFlush(user);
    }

    @Test
    public void 친구목록_조회_테스트() {
        // given
        List<User> friendsToFind = List.of(friend1, friend2, nonFriend);

        // when
        List<Friend> foundFriends = friendJpaRepository.findByUserAndFriendIds(mainUser,
            friendsToFind);

        // then
        assertThat(foundFriends).hasSize(2);

        // 찾은 친구 관계가 저장된 친구 관계와 동일한지 확인
        assertThat(foundFriends)
            .extracting(Friend::getId)
            .containsExactlyInAnyOrderElementsOf(
                savedFriends.stream().map(Friend::getId).toList()
            );

        // nonFriend는 결과에 포함되지 않아야 함
        assertThat(foundFriends)
            .extracting(friend -> friend.getFriend().getId())
            .doesNotContain(nonFriend.getId());

    }

}