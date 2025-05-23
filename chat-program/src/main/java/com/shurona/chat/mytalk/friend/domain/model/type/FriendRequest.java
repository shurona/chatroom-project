package com.shurona.chat.mytalk.friend.domain.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FriendRequest {

    REQUESTED(RequestStatus.REQUEST),
    ACCEPTED(RequestStatus.ACCEPTED),
    REFUSED(RequestStatus.REFUSED),
    BANNED(RequestStatus.BANNED);
//
    private final String status;

    public static class RequestStatus {

        public static final String REQUEST = "FRIEND_REQUEST";
        public static final String ACCEPTED = "FRIEND_ACCEPTED";
        public static final String REFUSED = "FRIEND_REFUSED";
        public static final String BANNED = "FRIEND_BANNED";

    }
}
