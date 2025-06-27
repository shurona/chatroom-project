package com.shurona.chat.mytalk.friend.presentation.rest;

import com.shurona.chat.mytalk.common.dto.CommonResponseDto;
import com.shurona.chat.mytalk.common.security.UserDetailsImpl;
import com.shurona.chat.mytalk.friend.application.FriendService;
import com.shurona.chat.mytalk.friend.presentation.dto.FriendRequestResponseDto;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
@RestController
public class FriendRestController {

    private final FriendService friendService;
    private final UserService userService;

    /**
     * 친구 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponseDto<List<FriendRequestResponseDto>>> FriendListPage(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // token의 유저 정보 조회
        User user = userService.findUserById(userDetails.userId());

        // 친구 목록 조회
        List<FriendRequestResponseDto> friendList = FriendRequestResponseDto.from(
            friendService.findAcceptedFriendListByUser(user)
        );

        return ResponseEntity.ok(
            CommonResponseDto.ofData(
                friendList
            )
        );
    }

}
