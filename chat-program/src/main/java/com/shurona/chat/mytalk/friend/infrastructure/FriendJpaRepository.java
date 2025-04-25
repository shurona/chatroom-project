package com.shurona.chat.mytalk.friend.infrastructure;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendJpaRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM friend f JOIN FETCH f.friend WHERE f.user = :user AND f.request = :request")
    List<Friend> findUserListByUserAndRequest(User user, FriendRequest request);

    Optional<Friend> findByUserAndFriend(User user, User friend);


}
