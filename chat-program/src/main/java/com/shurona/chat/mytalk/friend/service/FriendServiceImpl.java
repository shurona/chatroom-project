package com.shurona.chat.mytalk.friend.service;

import static com.shurona.chat.mytalk.friend.common.exception.FriendErrorCode.INVALID_INPUT;

import com.shurona.chat.mytalk.friend.common.exception.FriendErrorCode;
import com.shurona.chat.mytalk.friend.common.exception.FriendException;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.infrastructure.FriendJpaRepository;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest.RequestStatus;
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
public class FriendServiceImpl implements FriendService {

    private final FriendJpaRepository friendRepository;

    @Transactional
    @Override
    public Friend saveFriend(User user, User friend) {

        // 같은 사람이면 금지
        if (user.getId() == friend.getId()) {
            throw new FriendException(INVALID_INPUT.getStatus(), INVALID_INPUT.getMessage());
        }

        Optional<Friend> dbFriend = friendRepository.findByUserAndFriend(user, friend);

        // 이미 존재 하는 경우에는 접근 제한
        if (dbFriend.isPresent()) {
            throw new FriendException(INVALID_INPUT.getStatus(), INVALID_INPUT.getMessage());
        }

        Friend newFriend = Friend.newFriend(user, friend);
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
        return friendRepository.findUserListByUserAndRequest(user, FriendRequest.REQUESTED);
    }

    @Override
    public Friend findFriendByUserAndFriend(User user, User friend) {
        Optional<Friend> friendInfo = friendRepository.findByUserAndFriend(friend, user);


        return null;
    }

    @Transactional
    @Override
    public Friend changeStatusById(Long friendId, FriendRequest status) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(
            () -> new FriendException(INVALID_INPUT.getStatus(), INVALID_INPUT.getMessage()));

        if (status.equals(FriendRequest.ACCEPTED)) {
            friend.acceptFriendRequest();
        } else if (status.equals(FriendRequest.REFUSED)) {
            friend.refuseFriendRequest();
        }
        return friend;
    }
}
