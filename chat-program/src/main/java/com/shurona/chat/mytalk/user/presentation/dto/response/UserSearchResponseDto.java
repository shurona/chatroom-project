package com.shurona.chat.mytalk.user.presentation.dto.response;

import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.Map;

public record UserSearchResponseDto(
    Long userId,
    String nickName,
    String description,
    String phoneNumber,
    FriendRequest requested
) {

    public static UserSearchResponseDto from(User user, Map<Long, FriendRequest> requestStatusMap) {
        return new UserSearchResponseDto(
            user.getId(),
            user.getNickName(),
            user.getDescription(),
            user.getPhoneNumber().getPhoneNumber(),
            requestStatusMap.getOrDefault(user.getId(), null)
        );
    }

}
