package com.shurona.chat.mytalk.user.infrastructure.jpa;

import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.vo.UserPhoneNumber;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String username);

    Optional<User> findByPhoneNumber(UserPhoneNumber userPhoneNumber);

    /**
     * 닉네임 조건 조회
     */
    Page<User> findByNickNameContaining(String nickName, Pageable pageable);

    /**
     * 친구가 아닌 유저 및 닉네임 조건 조회
     */
    @Query("SELECT u FROM User u " +
        "WHERE u.id NOT IN (SELECT f.friend.id FROM friend f WHERE f.user.id = :userId and f.request = 'ACCEPTED') "
        +
        "and u.id <> :userId " +
        "AND u.nickName LIKE CONCAT('%', :nickName, '%')")
    Page<User> findNonFriendsByUserIdAndNicknameContaining(
        Long userId,
        String nickName,
        Pageable pageable
    );

}
