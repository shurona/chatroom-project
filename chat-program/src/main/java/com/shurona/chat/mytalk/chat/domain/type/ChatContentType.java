package com.shurona.chat.mytalk.chat.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChatContentType {

    TEXT("TEXT");

    private final String type;

}
