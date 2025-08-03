package com.shurona.chat.mytalk.friend.infrastructure.jpa;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendJpaRepository extends JpaRepository<Friend, Long> {

    /**
     * 내가 친구에서 특정 상태를 요청한 목록을 갖고 온다.
     */
    @Query("SELECT f FROM friend f JOIN FETCH f.friend JOIN FETCH f.user WHERE f.user = :user AND f.request = :request")
    List<Friend> findUserListByUserAndRequest(User user, FriendRequest request);

    /**
     * 친구가 나를 특정 상태로 요청을 한 목록을 갖고 온다.
     */
    @Query("SELECT f FROM friend f JOIN FETCH f.friend JOIN FETCH f.user WHERE f.friend = :friend AND f.request = :request")
    List<Friend> findUserListByFriendAndRequest(User friend, FriendRequest request);


    /**
     * 유저와 친구를 기준으로 데이터를 갖고 온다.
     */
    Optional<Friend> findByUserAndFriend(User user, User friend);


    /**
     * 유저와 친구 목록을 기준으로 데이터를 갖고 온다.
     */
    @Query("SELECT f FROM friend f WHERE f.user = :user and f.friend in :friendList")
    List<Friend> findByUserAndFriendIds(User user, List<User> friendList);
}
