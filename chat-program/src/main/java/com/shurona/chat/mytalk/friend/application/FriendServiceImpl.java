package com.shurona.chat.mytalk.friend.application;

import static com.shurona.chat.mytalk.friend.common.exception.FriendErrorCode.FRIEND_ALREADY_EXIST;
import static com.shurona.chat.mytalk.friend.common.exception.FriendErrorCode.INVALID_INPUT;

import com.shurona.chat.mytalk.friend.common.exception.FriendException;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.friend.domain.service.FriendChecker;
import com.shurona.chat.mytalk.friend.infrastructure.FriendJpaRepository;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendServiceImpl implements FriendService, FriendChecker {

    private final FriendJpaRepository friendRepository;

    @Transactional
    @Override
    public Friend saveFriend(User user, User friend) {

        // 같은 사람이면 금지
        if (user.getId() == friend.getId()) {
            throw new FriendException(INVALID_INPUT);
        }

        // 이미 친구 상태인지 확인하고 존재하면 에러 처리
        Optional<Friend> dbFriend = friendRepository.findByUserAndFriend(user, friend);
        if (dbFriend.isPresent()) {
            throw new FriendException(FRIEND_ALREADY_EXIST);
        }

        // 반대로 이미 존재하는지 확인한다.
        Optional<Friend> checkMutualFriendship = friendRepository.findByUserAndFriend(friend, user);

        // 상대방이 친구 수락을 했으면 자동으로 수락을 한다.
        Friend newFriend;
        if (checkMutualFriendship.isPresent()) {
            newFriend = Friend.acceptMutualFriend(user, friend);
        } else {
            newFriend = Friend.newFriend(user, friend);
        }

        return friendRepository.save(newFriend);

    }

    @Override
    public Friend findFriendById(Long id) {
        return friendRepository.findById(id)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST));
    }

    @Override
    public List<Friend> findAcceptedFriendListByUser(User user) {
        return friendRepository.findUserListByUserAndRequest(user, FriendRequest.ACCEPTED);
    }

    @Override
    public List<Friend> findRequestedFriendListByUser(User user) {
        return friendRepository.findUserListByFriendAndRequest(user, FriendRequest.REQUESTED);
    }


    @Override
    public Optional<Friend> findFriendByUserAndFriend(User user, User friend) {
        return friendRepository.findByUserAndFriend(user, friend);
    }

    @Transactional
    @Override
    public Friend changeStatusById(Long friendId, FriendRequest status) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(
            () -> new FriendException(INVALID_INPUT));

        if (status.equals(FriendRequest.ACCEPTED)) {
            friend.acceptFriendRequest();
        } else if (status.equals(FriendRequest.REFUSED)) {
            friend.refuseFriendRequest();
        } else if (status.equals(FriendRequest.BANNED)) {
            friend.bannedFriendRequest();
        }
        return friend;
    }


}
