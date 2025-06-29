package com.shurona.chat.mytalk.friend.presentation.dto.response;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import java.time.LocalDateTime;
import java.util.List;

public record FriendResponseDto(
    Long id,
    String nickName,
    String description,
    LocalDateTime createdAt,
    Boolean isOnline // TODO: online 확인 로직 추가
) {

    public static List<FriendResponseDto> from(List<Friend> friendList) {
        return friendList.stream().map(
                (friend) -> new FriendResponseDto(
                    friend.getId(),
                    friend.getFriend().getNickName(),
                    friend.getFriend().getDescription(),
                    friend.getCreatedAt(), true))
            .toList();
    }

}
