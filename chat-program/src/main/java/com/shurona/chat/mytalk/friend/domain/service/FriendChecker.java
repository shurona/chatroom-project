package com.shurona.chat.mytalk.friend.domain.service;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.Optional;

public interface FriendChecker {

    /*
        두 명이 친구인지 확인하는 함수
     */
    public Optional<Friend> findFriendByUserAndFriend(User user, User friend);

}
