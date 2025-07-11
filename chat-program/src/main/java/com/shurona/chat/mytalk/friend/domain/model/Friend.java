package com.shurona.chat.mytalk.friend.domain.model;

import static com.shurona.chat.mytalk.friend.common.exception.FriendErrorCode.INVALID_INPUT;

import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.friend.common.exception.FriendException;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "friend")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User friend;

    //    @Convert(converter = FriendRequestConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private FriendRequest request;

    @Column(name = "rcv_time")
    private LocalDateTime receiveTime;

    @Column
    private boolean banned;

    /*
        처음 친구를 만드는 경우
     */
    public static Friend newFriend(User user, User friend) {
        Friend newFriend = new Friend();

        newFriend.user = user;
        newFriend.friend = friend;
        newFriend.request = FriendRequest.REQUESTED;
        newFriend.banned = false;

        return newFriend;
    }

    /*
        상대방이 이미 수락했으면 자동으로 친구 수락
     */
    public static Friend acceptMutualFriend(User user, User friend) {
        Friend newFriend = new Friend();

        newFriend.user = user;
        newFriend.friend = friend;
        newFriend.request = FriendRequest.ACCEPTED;
        newFriend.banned = false;
        newFriend.receiveTime = LocalDateTime.now();

        return newFriend;
    }

    public void acceptFriendRequest() {
        if (this.request != FriendRequest.REQUESTED) {
            throw new FriendException(INVALID_INPUT);
        }
        this.request = FriendRequest.ACCEPTED;
        this.receiveTime = LocalDateTime.now();
    }

    public void refuseFriendRequest() {
        if (this.request != FriendRequest.REQUESTED) {
            throw new FriendException(INVALID_INPUT);
        }
        this.request = FriendRequest.REFUSED;
        this.receiveTime = LocalDateTime.now();
    }

    public void bannedFriendRequest() {
        // 이미 밴 되어 있는 상태면 불필요한 입력은 막는다.
        if (this.request == FriendRequest.BANNED) {
            throw new FriendException(INVALID_INPUT);
        }
        this.request = FriendRequest.BANNED;
        this.receiveTime = LocalDateTime.now();
    }
}
