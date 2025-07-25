package com.shurona.chat.mytalk.chat.presentation.dto.endpoint;

import java.util.List;

public record ReadRequest(
    List<Long> messageIds
) {

}
