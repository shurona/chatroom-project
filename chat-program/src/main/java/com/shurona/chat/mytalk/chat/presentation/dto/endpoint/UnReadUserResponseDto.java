package com.shurona.chat.mytalk.chat.presentation.dto.endpoint;

import java.util.Map;

public record UnReadUserResponseDto(
    Map<Long, Integer> readStatusMap
) {

}
