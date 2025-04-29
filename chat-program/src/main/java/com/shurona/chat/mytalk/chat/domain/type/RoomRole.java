package com.shurona.chat.mytalk.chat.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomRole {

    ADMIN("ADMIN"),
    MEMBER("MEMBER");

    private final String role;
}
