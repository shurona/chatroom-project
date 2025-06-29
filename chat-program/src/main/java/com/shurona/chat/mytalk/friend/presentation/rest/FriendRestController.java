package com.shurona.chat.mytalk.friend.presentation.rest;

import com.shurona.chat.mytalk.common.dto.CommonResponseDto;
import com.shurona.chat.mytalk.common.security.UserDetailsImpl;
import com.shurona.chat.mytalk.friend.application.FriendService;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.friend.presentation.dto.request.FriendNewRequestDto;
import com.shurona.chat.mytalk.friend.presentation.dto.response.FriendRequestResponseDto;
import com.shurona.chat.mytalk.friend.presentation.dto.response.FriendResponseDto;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<CommonResponseDto<List<FriendResponseDto>>> friendListPage(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // token의 유저 정보 조회
        User user = userService.findUserById(userDetails.userId());

        // 친구 목록 조회
        List<FriendResponseDto> friendList = FriendResponseDto.from(
            friendService.findAcceptedFriendListByUser(user)
        );

        return ResponseEntity.ok(
            CommonResponseDto.ofData(
                friendList
            )
        );
    }

    /**
     * 친구 추가
     */
    @PostMapping
    public ResponseEntity<?> requestFriend(
        @RequestBody FriendNewRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        User userInfo = userService.findUserById(userDetails.userId());
        User friendRequestUser = userService.findUserById(requestDto.userId());

        // 친구 추가
        friendService.saveFriend(userInfo, friendRequestUser);

        return ResponseEntity.ok().build();
    }

    /**
     * 친구 요청 목록 조회
     */
    @GetMapping("/requests")
    public ResponseEntity<CommonResponseDto<List<FriendRequestResponseDto>>> friendRequestList(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userService.findUserById(userDetails.userId());

        List<FriendRequestResponseDto> friendResponseDtoList = FriendRequestResponseDto.from(
            friendService.findRequestedFriendListByUser(user)
        );

        return ResponseEntity.ok(
            CommonResponseDto.ofData(
                friendResponseDtoList
            )
        );
    }

    /*
        유저들의 상태 변경을 위한 요청들
     */
    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("id") Long friendId
    ) {
        User user = userService.findUserById(userDetails.userId());

        Friend friendById = friendService.findFriendById(friendId);
        friendService.changeStatusById(friendById.getId(), FriendRequest.ACCEPTED);

        // 수락을 했으니 다음 것도 진행
        Friend anotherSide = friendService.saveAcceptFriend(friendById.getFriend(),
            friendById.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refuse")
    public ResponseEntity<?> refuseFriendRequest(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("id") Long friendId
    ) {
        User user = userService.findUserById(userDetails.userId());

        Friend friendById = friendService.findFriendById(friendId);
        friendService.changeStatusById(friendById.getId(), FriendRequest.REFUSED);

        return ResponseEntity.ok().build();

    }

}
