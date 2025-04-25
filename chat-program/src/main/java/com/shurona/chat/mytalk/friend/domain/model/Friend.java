package com.shurona.chat.mytalk.friend.domain.model;

import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

        return newFriend;
    }

    public void acceptFriendRequest() {
        if (this.request != FriendRequest.REQUESTED) throw new IllegalStateException("이미 처리됨");
        this.request = FriendRequest.ACCEPTED;
    }
    public void refuseFriendRequest() {
        if (this.request != FriendRequest.REQUESTED) throw new IllegalStateException("이미 처리됨");
        this.request = FriendRequest.REFUSED;
    }
}
