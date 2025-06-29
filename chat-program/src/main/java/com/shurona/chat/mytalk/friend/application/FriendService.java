package com.shurona.chat.mytalk.friend.application;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Map;

public interface FriendService {

    /**
     * 단일 조회
     */
    public Friend findFriendById(Long id);

    /**
     * 친구 저장(요청으로 저장)
     */
    public Friend saveFriend(User user, User friend);

    /**
     * 친구 요청 승인 시 상대도 자동승인을 위한 함수
     */
    public Friend saveAcceptFriend(User user, User friend);

    /**
     * 유저를 기준으로 받아들인 친구 목록을 받아오기
     * // TODO: 패이징 추가
     */
    public List<Friend> findAcceptedFriendListByUser(User user);

    /**
     * 유저를 기준으로 친구 요청 목록 받아오기
     * // TODO: 패이징 추가
     */
    public List<Friend> findRequestedFriendListByUser(User user);

    /**
     * 유저를 기준으로 친구에 해당하는 유저 목록을 기준으로 목록을 조회하기
     */
    public Map<Long, FriendRequest> findRequestFriendStatusByUserAndFriendIds(
        User user, List<User> friendList);


    /**
     * 친구 상태 변경
     */
    public Friend changeStatusById(Long friendId, FriendRequest status);


}
