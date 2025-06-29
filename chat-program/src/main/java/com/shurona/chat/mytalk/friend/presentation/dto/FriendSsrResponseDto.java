package com.shurona.chat.mytalk.friend.presentation.dto;

import com.shurona.chat.mytalk.friend.domain.model.Friend;
import java.time.LocalDateTime;
import java.util.List;

public record FriendSsrResponseDto(
    Long id,
    String loginId,
    String description,
    LocalDateTime createdAt
) {

    public static List<FriendSsrResponseDto> from(List<Friend> friendList) {
        return friendList.stream().map(
                (friend) -> new FriendSsrResponseDto(
                    friend.getId(),
                    friend.getFriend().getLoginId(),
                    friend.getFriend().getDescription(),
                    friend.getCreatedAt()))
            .toList();
    }


}
