package com.shurona.chat.mytalk.friend.presentation.dto.response;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 친구를 요청한 유저들의 목록
 */
public record FriendRequestResponseDto(
    Long friendId,
    Long requestedUserId,
    String nickName,
    String description,
    LocalDateTime requestAt,
    Boolean isOnline // TODO: online 확인 로직 추가
) {

    public static List<FriendRequestResponseDto> from(List<Friend> friendList) {
        return friendList.stream().map(
                (friend) -> new FriendRequestResponseDto(
                    friend.getId(),
                    friend.getUser().getId(),
                    friend.getFriend().getNickName(),
                    friend.getFriend().getDescription(),
                    friend.getCreatedAt(), true))
            .toList();
    }

}
