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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

        // null 체크
        if (user == null || friend == null) {
            throw new FriendException(INVALID_INPUT);
        }

        // 같은 사람이면 금지
        if (user.getId().equals(friend.getId())) {
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
        Friend newFriend = createFriendByCondition(user, friend, checkMutualFriendship);

        return friendRepository.save(newFriend);
    }

    @Transactional
    @Override
    public Friend saveAcceptFriend(User user, User friend) {
        // null 체크
        if (user == null || friend == null) {
            throw new FriendException(INVALID_INPUT);
        }

        // 같은 사람이면 금지
        if (user.getId().equals(friend.getId())) {
            throw new FriendException(INVALID_INPUT);
        }

        // 이미 친구 상태인지 확인한다.
        Optional<Friend> dbFriend = friendRepository.findByUserAndFriend(user, friend);
        if (dbFriend.isPresent()) {
            // 밴 상태만 아니면 승인한다.
            if (!dbFriend.get().getRequest().equals(FriendRequest.BANNED)) {
                dbFriend.get().acceptFriendRequest();
                return dbFriend.get();
            } else {
                return null;
            }
        } else {
            return friendRepository.save(Friend.acceptMutualFriend(user, friend));
        }
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
    public Map<Long, FriendRequest> findRequestFriendStatusByUserAndFriendIds(
        User user, List<User> friendList) {

        List<Friend> friends = friendRepository.findByUserAndFriendIds(user, friendList);

        return friends.stream()
            .collect(Collectors.toMap(
                f -> f.getFriend().getId(), // 키(Key): 친구 ID
                Friend::getRequest         // 값(Value): 요청 객체
            ));
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

    /**
     * checkMutualFriendship 조건에 맞춰서 새로운 친구 객체 생성
     */
    private Friend createFriendByCondition(
        User user, User friend, Optional<Friend> checkMutualFriendship) {

        Friend newFriend;
        if (checkMutualFriendship.isPresent()) {
            Friend friendSide = checkMutualFriendship.get();
            FriendRequest friendStatus = friendSide.getRequest();

            switch (friendStatus) {
                case ACCEPTED:
                    // 상대방이 수락 상태면 승인을 한다.
                    newFriend = Friend.acceptMutualFriend(user, friend);
                    break;
                case REQUESTED:
                    // 상대방이 요청 상태면 둘 다 수락해준다.
                    newFriend = Friend.acceptMutualFriend(user, friend);
                    friendSide.acceptFriendRequest();
                    friendRepository.save(friendSide);
                    break;
                case REFUSED:
                    // 거절된 경우 다시 요청 가능하게 처리
                    newFriend = Friend.newFriend(user, friend);
                    break;
                case BANNED:
                    // 차단됐어도 상대가 모르도록 기본으로 처리
                    newFriend = Friend.newFriend(user, friend);
                    break;
                default:
                    // 그 외 상태는 새 친구 생성
                    newFriend = Friend.newFriend(user, friend);
                    break;
            }
        } else {
            // 상대방이 없으면 새 친구 요청 생성
            newFriend = Friend.newFriend(user, friend);
        }
        return newFriend;
    }


}
