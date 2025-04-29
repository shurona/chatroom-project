package com.shurona.chat.mytalk.friend.infrastructure;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendJpaRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM friend f JOIN FETCH f.friend JOIN FETCH f.user WHERE f.user = :user AND f.request = :request")
    List<Friend> findUserListByUserAndRequest(User user, FriendRequest request);

    // 나를 친구로 등록한 유저 중에 요청 상태인 것을 갖고 온다.
    @Query("SELECT f FROM friend f JOIN FETCH f.friend JOIN FETCH f.user WHERE f.friend = :friend AND f.request = :request")
    List<Friend> findUserListByFriendAndRequest(User friend, FriendRequest request);

    Optional<Friend> findByUserAndFriend(User user, User friend);


}
