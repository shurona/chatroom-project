package com.shurona.chat.mytalk.common.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> content,
    int page, // 현재 페이지
    int size, // 현재 사이즈
    long totalElements, // 총 갯수
    int totalPages // 총 페이지
) {

    public static <T> PageResponse<T> from(Page<?> pageInfo, List<T> dataList) {
        return new PageResponse<>(
            dataList,
            pageInfo.getNumber(),
            pageInfo.getSize(),
            pageInfo.getTotalElements(),
            pageInfo.getTotalPages());
    }
}

