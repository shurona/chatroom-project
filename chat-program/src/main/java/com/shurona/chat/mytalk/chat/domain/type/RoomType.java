package com.shurona.chat.mytalk.chat.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomType {

    PRIVATE("PRIVATE"),
    GROUP("GROUP");


    private final String type;

}
