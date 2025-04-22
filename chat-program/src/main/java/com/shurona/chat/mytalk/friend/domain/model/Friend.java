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

    public static Friend newFriend(User user, User friend) {
        Friend copy = new Friend();

        copy.user = user;
        copy.friend = friend;
        copy.request = FriendRequest.REQUESTED;
        copy.banned = false;

        return copy;
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
